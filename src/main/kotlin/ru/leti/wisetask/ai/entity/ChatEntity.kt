package ru.leti.wisetask.ai.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "chat")
class ChatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime,

    @OneToMany
    @Column(name = "user_id")
    val user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "chat")
    val messages: List<ChatMessage> = mutableListOf()
)