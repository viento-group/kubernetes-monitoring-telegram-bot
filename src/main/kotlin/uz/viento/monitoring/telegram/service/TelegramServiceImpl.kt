package uz.viento.monitoring.telegram.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.model.TelegramSendMessage
import uz.viento.monitoring.telegram.model.TelegramSendMessage.Companion.ParseMode
import uz.viento.monitoring.telegram.properties.TelegramProperties

@Service
class TelegramServiceImpl(
    private val telegramProperties: TelegramProperties,
    private val restTemplate: RestTemplate
) : TelegramService {
    override fun sendMessages(text: String, chatIds: Set<String>, botType: BotType) {
        val token = botType.getToken()
        val apiUrl = String.format(TELEGRAM_SEND_MESSAGE_ENDPOINT_TEMPLATE, token)

        for (chatId in chatIds) {
            try {
                val method = TelegramSendMessage(chatId, text, ParseMode.MARKDOWN_V2)
                sendMessage(method, apiUrl)
            } catch (e: Exception) {
                logger.warn("Error sending message to chat '$chatId' by bot $botType:\n$text", e)
            }
        }
    }

    private fun sendMessage(sendMessage: TelegramSendMessage, apiUrl: String) {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val httpEntity = HttpEntity<TelegramSendMessage>(sendMessage, headers)
        restTemplate.postForObject<Any?>(apiUrl, httpEntity)
    }

    private fun BotType.getToken() = when (this) {
        BotType.KUBEWATCH -> tryToFindBotToken(telegramProperties.kubewatchBotToken, "kubewatch", telegramProperties)
    }

    private companion object {
        private const val TELEGRAM_SEND_MESSAGE_ENDPOINT_TEMPLATE = "https://api.telegram.org/bot%s/sendMessage"
        private val logger = LoggerFactory.getLogger(TelegramServiceImpl::class.java)

        private fun tryToFindBotToken(
            scopeToken: String?,
            scopeTokenName: String,
            telegramProperties: TelegramProperties
        ) = scopeToken
            ?: telegramProperties.botToken
            ?: throw IllegalArgumentException("Can't find nor $scopeTokenName bot token nor global bot token")
    }
}