package ru.leti.wisetask.ai.config

import org.springframework.ai.chat.client.AdvisorParams
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource


@Configuration
class ChatModelConfig {

    @Bean
    fun chatClient(
        builder: ChatClient.Builder,
        @Value("classpath:/prompts/system-prompt.st") systemPrompt: Resource
    ): ChatClient {
        return builder
            .defaultSystem(systemPrompt)
            .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
            .build()
    }
}