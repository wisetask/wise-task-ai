package ru.leti.wisetask.ai.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val corsProperties: CorsProperties,
    private val multitenancyResolver: MultitenancyAuthenticationResolver
) {

    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowedMethods = corsProperties.allowedMethods
            allowedHeaders = corsProperties.allowedHeaders
            allowCredentials = corsProperties.allowCredentials
            maxAge = corsProperties.maxAge
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun authenticationManagerResolver(): AuthenticationManagerResolver<HttpServletRequest> {
        return JwtIssuerAuthenticationManagerResolver(multitenancyResolver)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { request ->
                request
                    .requestMatchers("/ai", "/graphiql/**").permitAll()
                    .requestMatchers("/actuator/prometheus", "/graphiql", "/actuator/health", "/actuator/health/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .cors { it.configurationSource(corsConfiguration()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .oauth2ResourceServer { it.authenticationManagerResolver(authenticationManagerResolver()) }
            .build()
    }
}
