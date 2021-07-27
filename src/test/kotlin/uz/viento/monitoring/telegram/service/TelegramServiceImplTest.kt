package uz.viento.monitoring.telegram.service

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import uz.viento.monitoring.AbstractTest
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.model.TelegramSendMessage
import uz.viento.monitoring.telegram.model.TelegramSendMessage.Companion.ParseMode

@TestPropertySource(properties = [
    "telegram.bot-token=${TelegramServiceImplTest.TELEGRAM_BOT_TOKEN}",
    "telegram.kubewatch-bot-token=${TelegramServiceImplTest.KUBEWATCH_TELEGRAM_BOT_TOKEN}"
])
internal class TelegramServiceImplTest : AbstractTest() {
    companion object {
        const val TELEGRAM_BOT_TOKEN = "TELEGRAM_BOT_TOKEN"
        const val KUBEWATCH_TELEGRAM_BOT_TOKEN = "KUBEWATCH_TELEGRAM_BOT_TOKEN"
    }

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var telegramService: TelegramServiceImpl

    @TestConfiguration
    class RestTemplateConfiguration {
        @Bean
        @Primary
        fun restTemplateMocked() = mock<RestTemplate>()
    }

    @Test
    fun `sendMessages - kubewatch bot`() {
        val simpleText = "Simple text"

        telegramService.sendMessages(simpleText, setOf("123", "456"), BotType.KUBEWATCH)

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        verify(restTemplate, times(1)).postForObject<Any?>(
            url = "https://api.telegram.org/bot$KUBEWATCH_TELEGRAM_BOT_TOKEN/sendMessage",
            HttpEntity(TelegramSendMessage("123", simpleText, ParseMode.MARKDOWN_V2), headers)
        )
        verify(restTemplate, times(1)).postForObject<Any?>(
            url = "https://api.telegram.org/bot$KUBEWATCH_TELEGRAM_BOT_TOKEN/sendMessage",
            HttpEntity(TelegramSendMessage("456", simpleText, ParseMode.MARKDOWN_V2), headers)
        )
    }
}