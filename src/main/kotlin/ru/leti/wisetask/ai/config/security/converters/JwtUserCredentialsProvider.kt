package ru.leti.wisetask.ai.config.security.converters

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import ru.leti.wisetask.ai.exception.JwtIssuerException

@Component
class JwtUserCredentialsProvider(
    private val converterMatchers: List<JwtConverterDecoder>
) {

    fun getConverter(issuer: String): Converter<Jwt, AbstractAuthenticationToken> {
        return converterMatchers.firstOrNull { it.match(issuer) }
            ?: throw JwtIssuerException("Не удалось найти соответствующего конвертера для '$issuer'.")
    }

    fun getDecoder(issuer: String): JwtDecoder {
        return converterMatchers.firstOrNull { it.match(issuer) }?.decoder()
            ?: throw JwtIssuerException("Не удалось найти соответствующего декодера '$issuer'.")
    }
}
