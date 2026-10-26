package ru.leti.wisetask.ai.config

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.stereotype.Component
import ru.leti.wisetask.ai.config.security.converters.JwtUserCredentialsProvider
import java.util.concurrent.ConcurrentHashMap

@Component
class MultitenancyAuthenticationResolver(
    private val provider: JwtUserCredentialsProvider
) : AuthenticationManagerResolver<String> {

    private val managers = ConcurrentHashMap<String, AuthenticationManager>()

    override fun resolve(issuer: String): AuthenticationManager {
        return managers.computeIfAbsent(issuer) { iss ->
            val authProvider = JwtAuthenticationProvider(provider.getDecoder(iss))
            authProvider.setJwtAuthenticationConverter(provider.getConverter(iss))
            AuthenticationManager { authProvider.authenticate(it) }
        }
    }
}
