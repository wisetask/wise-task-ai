package ru.leti.wisetask.ai.advisor

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.leti.wisetask.ai.entity.toMessage
import ru.leti.wisetask.ai.exception.NotFoundException
import ru.leti.wisetask.ai.repository.ChatRepository
import ru.leti.wisetask.ai.repository.MessageRepository
import java.util.UUID

@Component
class DbChatMemory(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) : ChatMemory {

    @Transactional
    override fun add(
        conversationId: String,
        messages: List<Message>
    ) {
        val chat = chatRepository.findById(UUID.fromString(conversationId))
            .orElseThrow { NotFoundException("Чат с id: $conversationId не найден") }
        messages.forEach { message ->
            chat.addChatMessage(message)
        }
        chatRepository.save(chat)
    }

    @Transactional(readOnly = true)
    override fun get(conversationId: String): List<Message> {
        return messageRepository.findMessagesByChatId(UUID.fromString(conversationId))
            .map { it.toMessage() }
    }

    @Transactional
    override fun clear(conversationId: String) {
        val chat = chatRepository.findById(UUID.fromString(conversationId))
            .orElseThrow { NotFoundException("Чат с id: $conversationId не найден") }
        chatRepository.delete(chat)
    }
}