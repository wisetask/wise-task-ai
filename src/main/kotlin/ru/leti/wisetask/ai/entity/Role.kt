package ru.leti.wisetask.ai.entity

import org.springframework.ai.chat.messages.*
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule


enum class Role(
    val role: String
) {
    USER("user") {
        override fun getMessage(payload: Map<String, Any>): UserMessage {
            return UserMessage(payload["text"] as String)
        }
    },
    ASSISTANT("assistant") {
        override fun getMessage(payload: Map<String, Any>): AssistantMessage {
            return AssistantMessage(payload["text"] as String)
        }
    },
    SYSTEM("system") {
        override fun getMessage(payload: Map<String, Any>): SystemMessage {
            return SystemMessage(payload["text"] as String)
        }
    },
    TOOL("tool"){
        override fun getMessage(payload: Map<String, Any>): ToolResponseMessage =
            objectMapper.convertValue(payload, ToolResponseMessage::class.java)
    };

    abstract fun getMessage(payload: Map<String, Any>): Message
    companion object {
        private val objectMapper = jsonMapper {
            addModule(kotlinModule())
        }
        fun getRole(roleName: String): Role =
            entries.first { it.role == roleName }
    }
}
