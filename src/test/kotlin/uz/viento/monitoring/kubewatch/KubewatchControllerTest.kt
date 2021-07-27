package uz.viento.monitoring.kubewatch

import org.junit.jupiter.api.Test

import org.mockito.kotlin.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import uz.viento.monitoring.AbstractWebTest
import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.kubewatch.service.KubewatchService
import java.time.OffsetDateTime

internal class KubewatchControllerTest : AbstractWebTest() {
    @MockBean
    private lateinit var kubewatchService: KubewatchService

    private companion object {
        // this body taken from real kubewatch request
        const val JSON_BODY = "{\"eventmeta\":{\"kind\":\"pod\",\"name\":\"kube-system/vpnkit-controller\",\"namespace\":\"simple-ns\",\"reason\":\"updated\"},\"text\":\"A `pod` in namespace `simple-ns` has been `updated`:\\n`kube-system/vpnkit-controller`\",\"time\":\"2021-07-26T20:32:26.2995036Z\"}"
    }

    @Test
    fun onKubewatchAction() {
        doNothing().whenever(kubewatchService).sendKubewatchStatus(any(), any(), any())

        mockMvc.perform(post("/kubewatch/123,456")
            .content(JSON_BODY)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)

        val expected = KubewatchData(
            eventMeta = mapOf(
                "kind" to "pod",
                "name" to "kube-system/vpnkit-controller",
                "namespace" to "simple-ns",
                "reason" to "updated"
            ),
            text = "A `pod` in namespace `simple-ns` has been `updated`:\n`kube-system/vpnkit-controller`",
            time = OffsetDateTime.parse("2021-07-26T20:32:26.2995036Z")
        )
        verify(kubewatchService, times(1)).sendKubewatchStatus(setOf("123", "456"), expected, null)
    }

    @Test
    internal fun `onKubewatchAction - bold format`() {
        doNothing().whenever(kubewatchService).sendKubewatchStatus(any(), any(), any())

        mockMvc.perform(post("/kubewatch/123,456")
            .param("format", "bold")
            .content(JSON_BODY)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)

        verify(kubewatchService, times(1)).sendKubewatchStatus(any(), any(), eq("bold"))
    }
}