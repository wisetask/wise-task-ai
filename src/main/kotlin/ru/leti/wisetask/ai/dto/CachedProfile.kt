package ru.leti.wisetask.ai.dto

data class CachedProfile(
    val id: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val patronymic: String? = null,
    val profileRole: String
)
