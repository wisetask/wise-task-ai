package ru.leti.wisetask.ai.config.security

import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.graphql.server.webflux.AuthenticationWebSocketInterceptor
import org.springframework.stereotype.Component

@Component
class MultitenancyWebSocketAuthInterceptor: AuthenticationWebSocketInterceptor {


}