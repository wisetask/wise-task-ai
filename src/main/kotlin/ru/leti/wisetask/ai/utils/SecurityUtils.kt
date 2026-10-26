package ru.leti.wisetask.ai.utils

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import ru.leti.wisetask.ai.dto.UserCredentials

object SecurityUtils {

    fun getUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return (authentication?.principal as UserCredentials).id
    }

    fun getUserIdOrAnonymous(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || authentication.principal !is UserCredentials) {
            return "anonymous"
        }
        return (authentication.principal as UserCredentials).id
    }


    fun getIssuer(jwt: Jwt): String? {
        val issuer = jwt.claims["iss"]
        return issuer?.toString()
    }
}
