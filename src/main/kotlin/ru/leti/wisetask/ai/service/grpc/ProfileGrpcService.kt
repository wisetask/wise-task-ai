package ru.leti.wisetask.ai.service.grpc

import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Component
import ru.leti.wise.task.profile.ProfileGrpc
import ru.leti.wise.task.profile.ProfileOuterClass
import ru.leti.wise.task.profile.ProfileServiceGrpc.ProfileServiceBlockingStub

@Observed
@Component
class ProfileGrpcService(
    private val profileService: ProfileServiceBlockingStub
) {

    fun getProfile(id: String): ProfileOuterClass.Profile {
        val request = ProfileGrpc.GetProfileRequest.newBuilder()
            .setProfileId(id)
            .build()
        return profileService.getProfile(request).profile
    }

    fun getProfileByEmail(email: String): ProfileOuterClass.Profile {
        val request = ProfileGrpc.GetProfileByEmailRequest.newBuilder()
            .setEmail(email)
            .build()
        return profileService.getProfileByEmail(request).profile
    }

    fun signUp(profile: ProfileOuterClass.Profile, isExternal: Boolean): ProfileOuterClass.Profile {
        val request = ProfileGrpc.SignUpRequest.newBuilder()
            .setProfile(profile)
            .setIsExternal(isExternal)
            .build()
        return profileService.signUp(request).profile
    }
}
