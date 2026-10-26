package ru.leti.wisetask.ai.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.leti.wisetask.ai.dto.CachedProfile
import ru.leti.wisetask.ai.entity.ProfileEntity
import ru.leti.wisetask.ai.repository.ProfileRepository
import ru.leti.wisetask.ai.service.grpc.ProfileGrpcService
import java.util.UUID

@Service
class ProfileService(
    private val profileGrpcService: ProfileGrpcService,
    private val profileRepository: ProfileRepository
) {

    @Lazy
    @Autowired
    private lateinit var self: ProfileService

    fun getProfileByEmail(email: String): CachedProfile {
        val userId = self.resolveUserIdByEmail(email)
        val profile = self.getProfile(userId)
        ensureLocalProfile(profile)
        return profile
    }

    @Cacheable(value = ["profile"], key = "#userId")
    fun getProfile(userId: String): CachedProfile {
        val profile = profileGrpcService.getProfile(userId)
        return CachedProfile(
            id = profile.id,
            email = profile.email,
            firstName = profile.firstName,
            lastName = profile.lastName,
            patronymic = profile.patronymic,
            profileRole = profile.profileRole.name
        )
    }

    @Cacheable(value = ["profile-by-email"], key = "#email", cacheManager = "localCacheManager")
    fun resolveUserIdByEmail(email: String): String {
        return profileGrpcService.getProfileByEmail(email).id
    }

    fun ensureLocalProfile(profile: CachedProfile): ProfileEntity {
        val profileId = UUID.fromString(profile.id)
        return profileRepository.findById(profileId).orElseGet {
            profileRepository.save(
                ProfileEntity(
                    id = profileId,
                    email = profile.email,
                    firstName = profile.firstName,
                    lastName = profile.lastName
                )
            )
        }
    }
}
