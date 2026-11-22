package ru.leti.wisetask.ai.config.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.stereotype.Component
import ru.leti.wisetask.ai.config.security.converters.JwtUserCredentialsProvider
import java.util.concurrent.ConcurrentHashMap

@Component
class IssuerAuthManagerProvider(
    private val provider: JwtUserCredentialsProvider
) {
    private val managers = ConcurrentHashMap<String, AuthenticationManager>()

    fun provide(issuer: String): AuthenticationManager {
        return managers.computeIfAbsent(issuer) { iss ->
            val authProvider = JwtAuthenticationProvider(provider.getDecoder(iss))
            authProvider.setJwtAuthenticationConverter(provider.getConverter(iss))
            AuthenticationManager { authProvider.authenticate(it) }
        }
    }
}