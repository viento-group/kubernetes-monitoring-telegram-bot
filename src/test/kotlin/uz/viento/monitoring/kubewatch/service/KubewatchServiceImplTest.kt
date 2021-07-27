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
    private companion object {
        const val ACTION_TEXT = "A `pod` in namespace `simple-ns` has been `updated`:\n`kube-system/vpnkit-controller`"
    }

    @MockBean
    private lateinit var telegramService: TelegramService

    @Autowired
    private lateinit var kubewatchServiceImpl: KubewatchServiceImpl

    @Test
    fun sendKubewatchStatus() {
        val chatIds = setOf("123", "456")

        doNothing().whenever(telegramService).sendMessages(ACTION_TEXT, chatIds, BotType.KUBEWATCH)

        val kubewatchData = KubewatchData(emptyMap(), ACTION_TEXT, OffsetDateTime.now())
        kubewatchServiceImpl.sendKubewatchStatus(chatIds, kubewatchData, null)

        verify(telegramService, times(1)).sendMessages(ACTION_TEXT, chatIds, BotType.KUBEWATCH)
    }

    @Test
    fun `sendKubewatchStatus - bold text`() {
        val expectedText = "A *pod* in namespace *simple-ns* has been *updated*:\n*kube-system/vpnkit-controller*"

        doNothing().whenever(telegramService).sendMessages(ACTION_TEXT, setOf("1"), BotType.KUBEWATCH)

        val kubewatchData = KubewatchData(emptyMap(), ACTION_TEXT, OffsetDateTime.now())
        kubewatchServiceImpl.sendKubewatchStatus(setOf("1"), kubewatchData, "bold")

        verify(telegramService, times(1)).sendMessages(expectedText, setOf("1"), BotType.KUBEWATCH)
    }

    @Test
    fun `sendKubewatchStatus - detailed text`() {
        val expectedText = """
            *New Kubewatch status:*
            kind: *pod*
            name: *kube-system/vpnkit-controller*
            namespace: *simple-ns*
            reason: *updated*
        """.trimIndent()

        doNothing().whenever(telegramService).sendMessages(ACTION_TEXT, setOf("1"), BotType.KUBEWATCH)

        val kubewatchData = KubewatchData(
            eventMeta = mapOf(
                "kind" to "pod",
                "name" to "kube-system/vpnkit-controller",
                "namespace" to "simple-ns",
                "reason" to "updated"
            ),
            text = ACTION_TEXT,
            time = OffsetDateTime.now()
        )
        kubewatchServiceImpl.sendKubewatchStatus(setOf("1"), kubewatchData, "detailed")

        verify(telegramService, times(1)).sendMessages(expectedText, setOf("1"), BotType.KUBEWATCH)
    }
}