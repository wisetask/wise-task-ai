package ru.leti.wisetask.ai.entity

import org.springframework.ai.chat.messages.*
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule


enum class Role(
    val role: String
) {
    USER("user") {
        override fun getMessage(payload: String): UserMessage {
            return UserMessage(payload)
        }
    },
    ASSISTANT("assistant") {
        override fun getMessage(payload: String): AssistantMessage {
            return AssistantMessage(payload)
        }
    },
    SYSTEM("system") {
        override fun getMessage(payload: String): SystemMessage {
            return SystemMessage(payload)
        }
    },
    TOOL("tool"){
        override fun getMessage(payload: String): ToolResponseMessage {
            return objectMapper.readValue(payload, ToolResponseMessage::class.java)
        }
    };

    abstract fun getMessage(payload: String): Message
    companion object {
        private val objectMapper = jsonMapper {
            addModule(kotlinModule())
        }
        fun getRole(roleName: String): Role =
            entries.first { it.role == roleName }
    }
}
