package ru.leti.wisetask.ai.config.security

import com.nimbusds.jwt.JWTParser
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.graphql.server.WebSocketGraphQlRequest
import org.springframework.graphql.server.WebSocketSessionInfo
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class WebSocketSecurityInterceptor(
    private val multitenancyProvider: IssuerAuthManagerProvider
) : WebSocketGraphQlInterceptor {

    companion object {
        private const val SECURITY_CONTEXT_ATTR = "WS_SECURITY_CONTEXT"
        private const val TOKEN_EXPIRES_AT_ATTR = "WS_TOKEN_EXPIRES_AT"
    }

    override fun handleConnectionInitialization(
        sessionInfo: WebSocketSessionInfo,
        connectionInitPayload: Map<String, Any>
    ): Mono<Any> {
        val authHeader = (connectionInitPayload[AUTHORIZATION] as? String ?: return Mono.error(
            AuthenticationCredentialsNotFoundException("No Auth Header")
        ))
        val token = authHeader.substring(7).trim()
        try {
            val claimsSet = JWTParser.parse(token).jwtClaimsSet
            val issuer = claimsSet.issuer
            val authManager = multitenancyProvider.provide(issuer)
            val authentication = authManager.authenticate(BearerTokenAuthenticationToken(token))
            val securityContext = SecurityContextHolder.createEmptyContext().apply {
                this.authentication = authentication
            }
            val expirationTime = claimsSet.expirationTime?.toInstant() ?: BadCredentialsException("No expiration time")
            sessionInfo.attributes[TOKEN_EXPIRES_AT_ATTR] = expirationTime
            sessionInfo.attributes[SECURITY_CONTEXT_ATTR] = securityContext
        } catch (_: Exception) {
            return Mono.error(BadCredentialsException("Invalid JWT token"))
        }
        return Mono.empty()
    }

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val wsRequest = request as? WebSocketGraphQlRequest
            ?: return chain.next(request)
        val securityContext = wsRequest.sessionInfo.attributes[SECURITY_CONTEXT_ATTR] as? SecurityContext
            ?: return Mono.error(AccessDeniedException("Unauthenticated WebSocket session"))
        val expiresAt = wsRequest.sessionInfo.attributes[TOKEN_EXPIRES_AT_ATTR] as? Instant
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
            return Mono.error(AccessDeniedException("JWT token has expired"))
        }
        return chain.next(request)
            .contextWrite { ctx -> ctx.put(SecurityContext::class.java.name, securityContext) }
    }
}
