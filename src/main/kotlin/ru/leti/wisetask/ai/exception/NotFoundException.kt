package ru.leti.wisetask.ai.exception


class NotFoundException(
    override val message: String
) : RuntimeException(message)