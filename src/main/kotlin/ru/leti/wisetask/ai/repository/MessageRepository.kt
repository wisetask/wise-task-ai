package ru.leti.wisetask.ai.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.leti.wisetask.ai.entity.MessageEntity
import java.util.UUID

interface MessageRepository: JpaRepository<MessageEntity, UUID> {
    @Query("SELECT m FROM MessageEntity m WHERE m.chat.id = :chatId ORDER BY m.createdAt")
    fun findMessagesByChatId(chatId: UUID): List<MessageEntity>
}