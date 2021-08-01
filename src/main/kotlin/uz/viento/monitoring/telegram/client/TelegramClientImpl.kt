package uz.viento.monitoring.telegram.client

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import uz.viento.monitoring.telegram.BotToken
import uz.viento.monitoring.telegram.model.TelegramSendMessage

@Component
class TelegramClientImpl(private val restTemplate: RestTemplate) : TelegramClient {
    override fun sendMessage(sendMessage: TelegramSendMessage, token: BotToken) {
        val apiUrl = String.format(TELEGRAM_ENDPOINT_TEMPLATE, token.token, TELEGRAM_SEND_MESSAGE_METHOD_NAME)
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        restTemplate.postForObject<Any?>(apiUrl, HttpEntity(sendMessage, headers))
    }

    companion object {
        private const val TELEGRAM_ENDPOINT_TEMPLATE = "https://api.telegram.org/bot%s/%s"
        private const val TELEGRAM_SEND_MESSAGE_METHOD_NAME = "sendMessage"
    }
}