package ru.leti.wisetask.ai.controller

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import ru.leti.graphql.types.Chat
import ru.leti.graphql.types.Message
import ru.leti.graphql.types.MessageChunk
import ru.leti.graphql.types.Profile
import ru.leti.graphql.types.UserMessage
import ru.leti.wisetask.ai.service.ChatService

@Controller
class ChatController(
    private val chatService: ChatService
) {

    @QueryMapping
    fun getMyProfile(): Profile {
        return chatService.getMyProfile()
    }

    @QueryMapping
    fun getMyChats(): List<Chat> {
        return chatService.getMyChats()
    }

    @QueryMapping
    fun getChatMessages(@Argument chatId: String): List<Message> {
        return chatService.getChatMessages(chatId)
    }

    @MutationMapping
    fun createNewChat(): Chat {
        return chatService.createNewChat()
    }

    @SubscriptionMapping
    fun sendMessage(@Argument message: UserMessage): Flux<MessageChunk> {
        return chatService.sendMessage(message)
    }

    @SchemaMapping(typeName = "Chat", field = "messages")
    fun messages(chat: Chat): List<Message> {
        return chatService.getChatMessages(chat.id)
    }

}
