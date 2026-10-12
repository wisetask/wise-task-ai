package ru.leti.wisetask.ai.config

import org.springframework.ai.chat.client.AdvisorParams
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import ru.leti.wisetask.ai.advisor.DbChatMemory


@Configuration
class ChatModelConfig(
    private val vectorStore: VectorStore,
    private val chatMemory: DbChatMemory
) {

    @Bean
    fun chatClient(
        builder: ChatClient.Builder,
        @Value("classpath:/prompts/system-prompt.st") systemPrompt: Resource
    ): ChatClient {
        return builder
            .defaultSystem(systemPrompt)
            .defaultAdvisors { spec ->
                AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT.accept(spec)
                spec.advisors(ragAdvisor(), historyAdvisor())
            }
            .build()
    }

    @Bean
    fun ragAdvisor(): Advisor {
        return RetrievalAugmentationAdvisor.builder().documentRetriever(
            vectorStoreDocumentRetriever()
        ).order(1).build()
    }

    @Bean
    fun vectorStoreDocumentRetriever(): VectorStoreDocumentRetriever {
        return VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .topK(4)
            .similarityThreshold(0.5)
            .build()
    }

    @Bean
    fun historyAdvisor(): MessageChatMemoryAdvisor {
        return MessageChatMemoryAdvisor.builder(chatMemory).build()
    }
}