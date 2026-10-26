package ru.leti.wisetask.ai.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import ru.leti.wisetask.ai.config.JwtProperties

@Configuration
class JwtConfiguration(
    private val jwtProperties: JwtProperties
) {

    @Bean
    fun jwtInternalDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(jwtProperties.publicKey).build()
    }
}
