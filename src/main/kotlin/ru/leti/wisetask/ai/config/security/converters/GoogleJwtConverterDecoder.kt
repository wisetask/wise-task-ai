package ru.leti.wisetask.ai.config.security.converters

import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import ru.leti.wisetask.ai.config.OAuth2IssuerProperties
import ru.leti.wisetask.ai.dto.CachedProfile
import ru.leti.wisetask.ai.dto.UserCredentials
import ru.leti.wisetask.ai.exception.JwtIssuerException
import ru.leti.wisetask.ai.service.ProfileService
import ru.leti.wisetask.ai.service.grpc.ProfileGrpcService
import ru.leti.wise.task.profile.ProfileOuterClass

@Component
class GoogleJwtConverterDecoder(
    private val profileService: ProfileService,
    private val oAuth2Properties: OAuth2IssuerProperties,
    private val profileGrpcService: ProfileGrpcService
) : JwtConverterDecoder {

    override fun match(issuer: String?): Boolean {
        return issuer != null && issuer == oAuth2Properties.googleIssuer
    }

    override fun decoder(): JwtDecoder {
        return JwtDecoders.fromIssuerLocation(oAuth2Properties.googleIssuer)
    }

    override fun convert(jwt: Jwt): UserCredentials {
        val iss = jwt.issuer?.toExternalForm()
            ?: throw JwtIssuerException("Отсутствует Issuer claim")
        if (iss != oAuth2Properties.googleIssuer) {
            throw JwtIssuerException("JWT issuer не является Google")
        }
        val email = jwt.getClaimAsString("email")
            ?: throw JwtIssuerException("Отсутствует email claim")
        val profile = try {
            profileService.getProfileByEmail(email)
        } catch (e: StatusRuntimeException) {
            if (e.status.code != Status.Code.NOT_FOUND) {
                throw e
            }
            signUpProfile(jwt)
        }
        val grantedAuthorities = listOf(SimpleGrantedAuthority("ROLE_${profile.profileRole}"))
        return UserCredentials(profile.id, profile.email, profile.profileRole, grantedAuthorities)
    }

    private fun signUpProfile(jwt: Jwt): CachedProfile {
        val email = jwt.getClaimAsString("email")
        val firstName = jwt.getClaimAsString("given_name")
        val lastName = jwt.getClaimAsString("family_name")
        if (email == null || firstName == null || lastName == null) {
            throw JwtIssuerException(
                "Токен идентификации не содержит основных 'claim' для создания нового пользователя"
            )
        }
        val profile = ProfileOuterClass.Profile.newBuilder()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setProfileRole(ProfileOuterClass.Role.USER)
            .build()
        val created = profileGrpcService.signUp(profile, true)
        return CachedProfile(
            id = created.id,
            email = created.email,
            firstName = created.firstName,
            lastName = created.lastName,
            patronymic = created.patronymic,
            profileRole = created.profileRole.name
        )
    }
}
