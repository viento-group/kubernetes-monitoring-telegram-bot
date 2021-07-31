package uz.viento.monitoring.prometheus.service

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import uz.viento.monitoring.AbstractTest
import uz.viento.monitoring.prometheus.DefaultPrometheusDataTextConverter
import uz.viento.monitoring.prometheus.PrometheusTestData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService

internal class PrometheusServiceImplTest : AbstractTest() {
    @Autowired
    private lateinit var prometheusServiceImpl: PrometheusServiceImpl

    @MockBean
    private lateinit var telegramService: TelegramService

    @Test
    fun sendPrometheusAlert() {
        doNothing().whenever(telegramService).sendMessages(any(), any(), any())

        prometheusServiceImpl.sendPrometheusAlert(PrometheusTestData.PROMETHEUS_DATA, setOf("123", "456"))

        val expectedText = DefaultPrometheusDataTextConverter.convert(PrometheusTestData.PROMETHEUS_DATA)
        verify(telegramService, times(1))
            .sendMessages(expectedText, setOf("123", "456"), BotType.PROMETHEUS_ALERT_MANAGER)
    }
}