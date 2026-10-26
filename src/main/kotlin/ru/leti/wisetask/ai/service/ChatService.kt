package ru.leti.wisetask.ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import ru.leti.graphql.types.Chat
import ru.leti.graphql.types.Message
import ru.leti.graphql.types.MessageChunk
import ru.leti.graphql.types.MessageType
import ru.leti.graphql.types.Profile
import ru.leti.graphql.types.UserMessage
import ru.leti.wisetask.ai.entity.ChatEntity
import ru.leti.wisetask.ai.exception.NotFoundException
import ru.leti.wisetask.ai.repository.ChatRepository
import ru.leti.wisetask.ai.repository.MessageRepository
import ru.leti.wisetask.ai.repository.ProfileRepository
import ru.leti.wisetask.ai.utils.SecurityUtils
import java.util.UUID

@Service
class ChatService(
    private val chatClient: ChatClient,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val profileRepository: ProfileRepository,
    private val profileService: ProfileService
) {
    companion object {
        const val NEW_CHAT_TITLE = "Новый чат"
    }

    fun sendMessage(message: UserMessage): Flux<MessageChunk> {
        val streamId = UUID.randomUUID().toString()
        return chatClient.prompt(message.content)
            .advisors { spec -> spec.param(ChatMemory.CONVERSATION_ID, message.chatId) }
            .stream()
            .content()
            .map { delta ->
                MessageChunk(
                    chatId = message.chatId,
                    streamId = streamId,
                    delta = delta,
                    done = false
                )
            }
            .concatWith(
                Flux
                    .just(
                        MessageChunk(
                            chatId = message.chatId,
                            streamId = streamId,
                            delta = "",
                            done = true
                        )
                    )
            )
    }

    fun getMyProfile(): Profile {
        val profileId = UUID.fromString(SecurityUtils.getUserId())
        val profile = profileRepository.findById(profileId)
            .orElseThrow { NotFoundException("Профиль с id: $profileId не найден") }
        return Profile(
            email = profile.email,
            first_name = profile.firstName,
            last_name = profile.lastName
        )
    }

    fun getMyChats(): List<Chat> {
        return chatRepository.findAllByProfileId(UUID.fromString(SecurityUtils.getUserId()))
            .map { it.toChat() }
    }

    fun getChatMessages(chatId: String): List<Message> {
        getOwnedChat(chatId)
        return messageRepository.findMessagesByChatId(UUID.fromString(chatId)).map { message ->
            Message(
                id = message.id.toString(),
                type = MessageType.entries.first { entry -> entry.name == message.role.name },
                payload = message.payload["text"] as String?,
                createdAt = message.createdAt
            )
        }
    }

    @Transactional
    fun createNewChat(): Chat {
        val profile = profileService.ensureLocalProfile(
            profileService.getProfile(SecurityUtils.getUserId())
        )
        return chatRepository.save(ChatEntity(title = NEW_CHAT_TITLE, profile = profile)).toChat()
    }

    private fun getOwnedChat(chatId: String): ChatEntity {
        val chat = chatRepository.findById(UUID.fromString(chatId))
            .orElseThrow { NotFoundException("Чат с id: $chatId не найден") }
        if (chat.profile.id != UUID.fromString(SecurityUtils.getUserId())) {
            throw NotFoundException("Чат с id: $chatId не найден")
        }
        return chat
    }
}

fun ChatEntity.toChat(): Chat {
    return Chat(
        id = id.toString(),
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
