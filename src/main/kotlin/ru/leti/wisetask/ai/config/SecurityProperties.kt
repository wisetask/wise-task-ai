package ru.leti.wisetask.ai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties("security.jwt")
data class JwtProperties(
    val publicKey: RSAPublicKey,
    val issuer: String
)

@ConfigurationProperties("security.cors")
data class CorsProperties(
    val allowedOrigins: List<String>,
    val allowedMethods: List<String>,
    val allowedHeaders: List<String>,
    val allowCredentials: Boolean,
    val maxAge: Long
)

@ConfigurationProperties("app.oauth2.trusted-issuers")
data class OAuth2IssuerProperties(
    val googleIssuer: String
)
