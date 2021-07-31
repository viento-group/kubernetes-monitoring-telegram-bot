package uz.viento.monitoring.telegram.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.TelegramTextUtils
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
            } catch (e: HttpClientErrorException.Unauthorized) {
                logger.error("Received unauthorized error response when trying to send telegram message. " +
                        "Make sure, that you have specified correct token!")
            } catch (e: HttpClientErrorException.BadRequest) {
                logger.error("Received '400 Bad Request' error response when sending telegram message. " +
                        "Response body: ${e.responseBodyAsString}")
            } catch (e: Exception) {
                logger.warn("Error sending message to chat '$chatId' by bot $botType:\n$text", e)
            }
        }
    }

    private fun sendMessage(sendMessage: TelegramSendMessage, apiUrl: String) {
        TelegramTextUtils.splitMessageTextToParts(sendMessage.text)
            .forEach { messagePart ->
                val sendMessagePart = sendMessage.copy(text = messagePart)
                val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
                restTemplate.postForObject<Any?>(apiUrl, HttpEntity(sendMessagePart, headers))
            }
    }

    private fun BotType.getToken() = when (this) {
        BotType.KUBEWATCH -> tryToFindBotToken(telegramProperties.kubewatchBotToken, "Kubewatch", telegramProperties)
        BotType.PROMETHEUS_ALERT_MANAGER -> tryToFindBotToken(telegramProperties.prometheusBotToken, "Prometheus AlertManager", telegramProperties)
    }

    private companion object {
        private const val TELEGRAM_SEND_MESSAGE_ENDPOINT_TEMPLATE = "https://api.telegram.org/bot%s/sendMessage"
        private val logger = LoggerFactory.getLogger(TelegramServiceImpl::class.java)

        private fun tryToFindBotToken(
            scopeToken: String?,
            scopeTokenName: String,
            telegramProperties: TelegramProperties
        ) = scopeToken?.ifEmpty { null }
            ?: telegramProperties.botToken?.ifEmpty { null }
            ?: throw IllegalArgumentException("Can't find nor $scopeTokenName bot token nor global bot token")
    }
}