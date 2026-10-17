package ru.leti.wisetask.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WiseTaskAi

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val application = runApplication<WiseTaskAi>(*args)
    log.info { "WiseTaskAi started successfully" }
    val chatModel = application.getBean(ChatModel::class.java)

    val prompt = Prompt(UserMessage("Привет, что ты можешь рассказать про алгоритм Краскала?"))

    chatModel.stream(prompt)
        .doOnNext { response ->
            val text = response.result?.output?.text
            if (!text.isNullOrEmpty()) {
                print(text)
            }
        }
        .blockLast()

    println()
}
