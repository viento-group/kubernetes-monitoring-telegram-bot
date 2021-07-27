package uz.viento.monitoring.kubewatch.service

import org.junit.jupiter.api.Test
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import uz.viento.monitoring.AbstractTest
import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService
import java.time.OffsetDateTime

internal class KubewatchServiceImplTest : AbstractTest() {
    @MockBean
    private lateinit var telegramService: TelegramService

    @Autowired
    private lateinit var kubewatchServiceImpl: KubewatchServiceImpl

    @Test
    fun sendKubewatchStatus() {
        val text = "Simple test"
        val chatIds = setOf("123", "456")

        doNothing().whenever(telegramService).sendMessages(text, chatIds, BotType.KUBEWATCH)

        val kubewatchData = KubewatchData(emptyMap(), text, OffsetDateTime.now())
        kubewatchServiceImpl.sendKubewatchStatus(chatIds, kubewatchData)

        verify(telegramService, times(1)).sendMessages(text, chatIds, BotType.KUBEWATCH)
    }
}