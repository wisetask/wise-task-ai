package ru.leti.wisetask.ai.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    val id: UUID? = null,

    val email: String,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "last_name")
    val lastName: String? = null
)
