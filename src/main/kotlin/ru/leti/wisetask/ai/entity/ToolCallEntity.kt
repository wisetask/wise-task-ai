package ru.leti.wisetask.ai.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ToolCallEntity(
    @Id
    val id: UUID? = null
)