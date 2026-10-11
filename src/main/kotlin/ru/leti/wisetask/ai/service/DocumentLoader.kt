package ru.leti.wisetask.ai.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import ru.leti.wisetask.ai.entity.LoadedDocumentEntity
import ru.leti.wisetask.ai.repository.LoadedDocumentRepository

private val log = KotlinLogging.logger {}

@Component
class DocumentLoader(
    private val documentRepository: LoadedDocumentRepository,
    private val resolver: ResourcePatternResolver,
    private val vectorStore: VectorStore,
) {
    companion object {
        const val CHUNK_SIZE = 200
    }

    @EventListener(classes =  [ApplicationReadyEvent::class])
    fun loadDocuments() {
        val resources = resolver.getResources("classpath:/knowledgebase/**/*.text").toList()
        val unknownResources = resources
            .map { resource -> resource to calculateContentHash(resource) }
            .filter { (resource, hash) ->
                val filename = resource.filename
                filename != null && !documentRepository.existsByFilenameAndContentHash(filename, hash)
            }
        unknownResources.forEach { (resource, hash) ->
            val documents = TextReader(resource).get()
            val textSplitter = TokenTextSplitter.builder().withChunkSize(CHUNK_SIZE).build()
            val chunks = textSplitter.apply(documents)
            log.info { "Adding new chunks: ${chunks.size}" }
            val loadedDocument = LoadedDocumentEntity(
                chunkCount = chunks.size,
                documentType = "tex",
                filename = resource.filename ?: "",
                contentHash = hash
            )
            documentRepository.save(loadedDocument)
        }
    }

    fun calculateContentHash(resource: Resource): String {
        return DigestUtils.md5DigestAsHex(resource.inputStream)
    }
}

