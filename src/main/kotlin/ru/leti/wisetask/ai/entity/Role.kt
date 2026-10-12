package ru.leti.wisetask.ai.entity

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage

enum class Role(
    val role: String
) {
    USER("user") {
        override fun getMessage(content: String): UserMessage {
            return UserMessage(content)
        }
    },
    ASSISTANT("assistant") {
        override fun getMessage(content: String): AssistantMessage {
            return AssistantMessage(content)
        }
    },
    SYSTEM("system") {
        override fun getMessage(content: String): SystemMessage {
            return SystemMessage(content)
        }
    };

    abstract fun getMessage(content: String): Message
    companion object {
        fun getRole(roleName: String): Role =
            entries.first { it.role == roleName }
    }
}
