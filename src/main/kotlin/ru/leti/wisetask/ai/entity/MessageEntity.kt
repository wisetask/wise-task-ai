package ru.leti.wisetask.ai.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.ai.chat.messages.Message
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "chat_message")
class MessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val payload: Map<String, Any>,

    @Enumerated(value = EnumType.STRING)
    @Column("message_type")
    val role: Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: ChatEntity? = null,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

)

fun MessageEntity.toMessage() = role.getMessage(payload)

fun Message.toEntity(chat: ChatEntity) = MessageEntity(
    role = Role.getRole(messageType.value),
    payload = mapOf("text" to (text ?: "")),
    chat = chat
)
