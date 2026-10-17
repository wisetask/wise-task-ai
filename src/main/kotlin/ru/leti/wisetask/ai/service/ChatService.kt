package ru.leti.wisetask.ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.leti.graphql.types.Chat
import ru.leti.graphql.types.MessageChunk
import ru.leti.graphql.types.UserMessage
import ru.leti.wisetask.ai.entity.ChatEntity
import ru.leti.wisetask.ai.repository.ChatRepository
import java.util.UUID

@Service
class ChatService(
    private val chatClient: ChatClient,
    private val chatRepository: ChatRepository
) {

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
}