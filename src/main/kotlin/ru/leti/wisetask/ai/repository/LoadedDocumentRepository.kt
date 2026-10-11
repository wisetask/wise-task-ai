package ru.leti.wisetask.ai.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.leti.wisetask.ai.entity.LoadedDocumentEntity
import java.util.UUID

@Repository
interface LoadedDocumentRepository : JpaRepository<LoadedDocumentEntity, UUID> {
    fun existsByFilenameAndContentHash(filename: String, contentHash: String): Boolean
}