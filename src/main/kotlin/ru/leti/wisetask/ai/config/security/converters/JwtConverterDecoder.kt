package ru.leti.wisetask.ai.config.security.converters

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import ru.leti.wisetask.ai.dto.UserCredentials

interface JwtConverterDecoder : Converter<Jwt, AbstractAuthenticationToken> {

    fun match(issuer: String?): Boolean

    fun decoder(): JwtDecoder
}
