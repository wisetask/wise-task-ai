package ru.leti.wisetask.ai.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Table(name = "user")
class UserEntity(
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val email: String,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user")
    val chats: List<ChatEntity> = mutableListOf()
)