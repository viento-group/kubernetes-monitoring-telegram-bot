package uz.viento.monitoring.telegram.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import uz.viento.monitoring.telegram.BotToken
import uz.viento.monitoring.telegram.BotTokenType
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.TelegramTextUtils
import uz.viento.monitoring.telegram.client.TelegramClient
import uz.viento.monitoring.telegram.model.TelegramSendMessage
import uz.viento.monitoring.telegram.model.TelegramSendMessage.Companion.ParseMode
import uz.viento.monitoring.telegram.properties.TelegramProperties

@Service
class TelegramServiceImpl(
    private val telegramProperties: TelegramProperties,
    private val telegramClient: TelegramClient
) : TelegramService {
    override fun sendMessages(text: String, chatIds: Set<String>, botType: BotType) {
        val token = botType.getToken()
        val textParts = TelegramTextUtils.splitMessageTextToParts(text)

        for (chatId in chatIds) {
            textParts.forEach { textPart ->
                val method = TelegramSendMessage(chatId, textPart, ParseMode.MARKDOWN_V2)
                try {
                    telegramClient.sendMessage(method, token)
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
    }

    private fun BotType.getToken() = when (this) {
        BotType.KUBEWATCH -> tryToFindBotToken(
            scopeToken = telegramProperties.kubewatchBotToken,
            scopeTokenType = BotTokenType.KUBEWATCH_BOT,
            telegramProperties = telegramProperties
        )

        BotType.PROMETHEUS_ALERT_MANAGER -> tryToFindBotToken(
            scopeToken = telegramProperties.prometheusBotToken,
            scopeTokenType = BotTokenType.PROMETHEUS_BOT,
            telegramProperties = telegramProperties
        )
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(TelegramServiceImpl::class.java)

        private fun tryToFindBotToken(
            scopeToken: String?,
            scopeTokenType: BotTokenType,
            telegramProperties: TelegramProperties
        ) = scopeToken?.ifEmpty { null }?.let { BotToken(it, scopeTokenType) }
            ?: telegramProperties.botToken?.ifEmpty { null }?.let { BotToken(it, BotTokenType.GLOBAL) }
            ?: throw IllegalArgumentException("Can't find nor ${scopeTokenType.getFriendlyName()} bot token nor global bot token")

        private fun BotTokenType.getFriendlyName() = when (this) {
            BotTokenType.GLOBAL -> "global"
            BotTokenType.KUBEWATCH_BOT -> "Kubewatch"
            BotTokenType.PROMETHEUS_BOT -> "Prometheus"
        }
    }
}