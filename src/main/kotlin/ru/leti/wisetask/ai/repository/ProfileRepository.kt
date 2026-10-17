package ru.leti.wisetask.ai.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.leti.wisetask.ai.entity.ProfileEntity
import java.util.UUID

@Repository
interface ProfileRepository: JpaRepository<ProfileEntity, UUID> {
}