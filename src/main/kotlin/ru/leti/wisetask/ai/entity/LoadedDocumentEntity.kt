package ru.leti.wisetask.ai.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "loaded_document")
class LoadedDocumentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "chunk_count")
    var chunkCount: Int,

    @Column(name = "loaded_at")
    val loadedAt: OffsetDateTime = OffsetDateTime.now(),

    val filename: String,

    @Column(name = "content_hash")
    val contentHash: String,

    @Column(name = "document_type")
    val documentType: String,
)