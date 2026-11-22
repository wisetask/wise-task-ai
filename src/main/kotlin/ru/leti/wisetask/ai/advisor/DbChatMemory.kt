package ru.leti.wisetask.ai.advisor

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.leti.wisetask.ai.entity.Role
import ru.leti.wisetask.ai.entity.toMessage
import ru.leti.wisetask.ai.exception.NotFoundException
import ru.leti.wisetask.ai.repository.ChatRepository
import java.util.UUID

@Component
class DbChatMemory(
    private val chatRepository: ChatRepository,
    @Value("\${ai.memory.max-messages:40}") private val maxMessages: Int
) : ChatMemory {

    @Transactional
    override fun add(
        conversationId: String,
        messages: List<Message>
    ) {
        val chat = chatRepository.findByIdWithHistory(UUID.fromString(conversationId))
            .orElseThrow { NotFoundException("Чат с id: $conversationId не найден") }
        messages.forEach { message ->
            chat.addChatMessage(message)
        }
        chatRepository.save(chat)
    }

    @Transactional(readOnly = true)
    override fun get(conversationId: String): List<Message> {
        val chat = chatRepository.findByIdWithHistory(UUID.fromString(conversationId))
            .orElseThrow { NotFoundException("Чат с id: $conversationId не найден") }
        val messagesToSkip = maxOf(0, chat.history.size - maxMessages)
        return chat.history
            .drop(messagesToSkip)
            .filter { it.role != Role.TOOL }
            .map { it.toMessage() }
    }

    @Transactional
    override fun clear(conversationId: String) {
        val chat = chatRepository.findById(UUID.fromString(conversationId))
            .orElseThrow { NotFoundException("Чат с id: $conversationId не найден") }
        chatRepository.delete(chat)
    }
}
