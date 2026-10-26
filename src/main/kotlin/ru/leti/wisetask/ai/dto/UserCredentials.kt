package ru.leti.wisetask.ai.dto

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class UserCredentials(
    val id: String,
    val email: String,
    val role: String,
    authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getName(): String = id

    override fun getCredentials(): Any = this

    override fun getPrincipal(): Any = this
}
