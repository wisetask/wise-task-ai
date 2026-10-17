package ru.leti.wisetask.ai.controller

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import ru.leti.graphql.types.MessageChunk
import ru.leti.graphql.types.UserMessage
import ru.leti.wisetask.ai.service.ChatService

@Controller
class ChatController(
    private val chatService: ChatService
) {

    @SubscriptionMapping
    fun sendMessage(@Argument message: UserMessage): Flux<MessageChunk> {
        return chatService.sendMessage(message)
    }

}