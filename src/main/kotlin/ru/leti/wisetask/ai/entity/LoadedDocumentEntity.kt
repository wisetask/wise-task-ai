package ru.leti.wisetask.ai.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.OffsetDateTime
import java.util.*

@Entity
class LoadedDocumentEntity(
    @Id
    val id: UUID? = null,
    var chunkCount: Int,
    val loadedAt: OffsetDateTime = OffsetDateTime.now(),
    val filename: String,
    val contentHash: String,
    val documentType: String,
)