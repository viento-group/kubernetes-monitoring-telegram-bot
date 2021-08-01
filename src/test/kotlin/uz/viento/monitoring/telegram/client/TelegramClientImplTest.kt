package uz.viento.monitoring.telegram.client

import org.junit.jupiter.api.Test

import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import uz.viento.monitoring.AbstractTest
import uz.viento.monitoring.telegram.BotToken
import uz.viento.monitoring.telegram.BotTokenType
import uz.viento.monitoring.telegram.model.TelegramSendMessage
import uz.viento.monitoring.telegram.model.TelegramSendMessage.Companion.ParseMode

internal class TelegramClientImplTest : AbstractTest() {
    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var telegramClientImpl: TelegramClientImpl

    @Test
    fun sendMessage() {
        val sendMessage = TelegramSendMessage("123", "Simple text", ParseMode.MARKDOWN_V2)
        val token = BotToken("ABC", BotTokenType.GLOBAL)

        val expectedHeaders = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val expectedApiUrl = "https://api.telegram.org/botABC/sendMessage"
        val expectedHttpEntity = HttpEntity(sendMessage, expectedHeaders)

        telegramClientImpl.sendMessage(sendMessage, token)

        verify(restTemplate, times(1)).postForObject<Any?>(expectedApiUrl, expectedHttpEntity)
    }
}