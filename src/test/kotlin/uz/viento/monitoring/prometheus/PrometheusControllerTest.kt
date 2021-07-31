package uz.viento.monitoring.prometheus

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import uz.viento.monitoring.AbstractWebTest
import uz.viento.monitoring.prometheus.service.PrometheusService

internal class PrometheusControllerTest : AbstractWebTest() {
    @MockBean
    private lateinit var prometheusService: PrometheusService

    @Test
    fun onPrometheusMessage() {
        doNothing().whenever(prometheusService).sendPrometheusAlert(any(), any())

        mockMvc.perform(post("/prometheus/123,456")
            .contentType(MediaType.APPLICATION_JSON)
            .content(PrometheusTestData.PROMETHEUS_ALERT_JSON))
            .andExpect(status().isNoContent)

        val chatIds = setOf("123", "456")
        verify(prometheusService, times(1)).sendPrometheusAlert(PrometheusTestData.PROMETHEUS_DATA, chatIds)
    }
}