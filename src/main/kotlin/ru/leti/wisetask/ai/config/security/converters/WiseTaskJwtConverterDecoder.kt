package ru.leti.wisetask.ai.config.security.converters

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import ru.leti.wisetask.ai.config.JwtProperties
import ru.leti.wisetask.ai.dto.UserCredentials
import ru.leti.wisetask.ai.exception.JwtIssuerException
import ru.leti.wisetask.ai.service.ProfileService

@Component
class WiseTaskJwtConverterDecoder(
    private val jwtProperties: JwtProperties,
    private val jwtInternalDecoder: JwtDecoder,
    private val profileService: ProfileService
) : JwtConverterDecoder {

    override fun match(issuer: String?): Boolean {
        return issuer != null && issuer == jwtProperties.issuer
    }

    override fun decoder(): JwtDecoder {
        return jwtInternalDecoder
    }

    override fun convert(jwt: Jwt): UserCredentials {
        val iss = jwt.getClaimAsString("iss")
            ?: throw JwtIssuerException("Отсутствует Issuer claim")
        if (iss != jwtProperties.issuer) {
            throw JwtIssuerException("JWT issuer не является WiseTask")
        }
        val email = jwt.getClaimAsString("email")
            ?: throw JwtIssuerException("Отсутствует email claim")
        val profile = profileService.getProfileByEmail(email)
        val grantedAuthorities = listOf(SimpleGrantedAuthority("ROLE_${profile.profileRole}"))
        return UserCredentials(profile.id, profile.email, profile.profileRole, grantedAuthorities)
    }
}
