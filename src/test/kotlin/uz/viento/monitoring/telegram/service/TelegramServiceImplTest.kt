package uz.viento.monitoring.telegram.service

import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import uz.viento.monitoring.AbstractTest
import uz.viento.monitoring.telegram.BotToken
import uz.viento.monitoring.telegram.BotTokenType
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.client.TelegramClient
import uz.viento.monitoring.telegram.model.TelegramSendMessage
import uz.viento.monitoring.telegram.model.TelegramSendMessage.Companion.ParseMode

internal class TelegramServiceImplTest : AbstractTest() {
    companion object {
        const val TELEGRAM_BOT_TOKEN = "TELEGRAM_BOT_TOKEN"
        const val KUBEWATCH_TELEGRAM_BOT_TOKEN = "KUBEWATCH_TELEGRAM_BOT_TOKEN"
    }

    abstract class BaseTelegramServiceImplTest : AbstractTest() {
        @MockBean
        protected lateinit var telegramClient: TelegramClient

        @Autowired
        protected lateinit var telegramService: TelegramServiceImpl
    }

    @TestPropertySource(properties = [
        "telegram.bot-token=${TELEGRAM_BOT_TOKEN}",
        "telegram.kubewatch-bot-token=${KUBEWATCH_TELEGRAM_BOT_TOKEN}"
    ])
    class TelegramServiceImplTestWithScopedBot : BaseTelegramServiceImplTest() {
        @Test
        fun `sendMessages - kubewatch bot`() {
            val simpleText = "Simple text"
            val expectedBotToken = BotToken(KUBEWATCH_TELEGRAM_BOT_TOKEN, BotTokenType.KUBEWATCH_BOT)

            telegramService.sendMessages(simpleText, setOf("123", "456"), BotType.KUBEWATCH)

            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("123", simpleText, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("456", simpleText, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
        }

        @Test
        fun `sendMessage - split to parts`() {
            val part1 = "a".repeat(4096)
            val part2 = "b".repeat(20)
            val expectedBotToken = BotToken(KUBEWATCH_TELEGRAM_BOT_TOKEN, BotTokenType.KUBEWATCH_BOT)

            telegramService.sendMessages(part1 + part2, setOf("123", "456"), BotType.KUBEWATCH)

            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("123", part1, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("123", part2, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("456", part1, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("456", part2, ParseMode.MARKDOWN_V2),
                token = expectedBotToken
            )
        }
    }

    @TestPropertySource(properties = ["telegram.bot-token=${TELEGRAM_BOT_TOKEN}"])
    class TelegramServiceImplTestWithGlobalToken : BaseTelegramServiceImplTest() {
        @Test
        fun `sendMessages - global bot`() {
            val simpleText = "Simple text"

            telegramService.sendMessages(simpleText, setOf("123"), BotType.KUBEWATCH)

            verify(telegramClient, times(1)).sendMessage(
                sendMessage = TelegramSendMessage("123", simpleText, ParseMode.MARKDOWN_V2),
                token = BotToken(TELEGRAM_BOT_TOKEN, BotTokenType.GLOBAL)
            )
        }
    }
}