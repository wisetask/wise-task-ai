package ru.leti.wisetask.ai.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.leti.wisetask.ai.entity.ChatEntity
import java.util.*

@Repository
interface ChatRepository : JpaRepository<ChatEntity, UUID> {
}