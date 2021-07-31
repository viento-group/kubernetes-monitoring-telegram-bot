package uz.viento.monitoring.prometheus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PrometheusDataTextConverterTest {
    @Test
    fun `default converter`() {
        val alert = PrometheusTestData.PROMETHEUS_DATA.alerts[0]
        val prometheusData = PrometheusTestData.PROMETHEUS_DATA.copy(alerts = listOf(alert, alert))

        val text = DefaultPrometheusDataTextConverter.convert(prometheusData)

        assertThat(text).isEqualTo("""
            *Status: Firing 🔥*

            *Active Alert List:*
            *Target disappeared from Prometheus target discovery\.*
            Status: Firing 🔥
            KubeScheduler has disappeared from Prometheus target discovery\.
            [Runbook URL](https://github\.com/kubernetes\-monitoring/kubernetes\-mixin/tree/master/runbook\.md\#alert\-name\-kubeschedulerdown)

            Alert Name: KubeSchedulerDown
            Severity: critical ❗️
            \-\-\-\-\-\-\-\-\-\-\-\-\-
            *Target disappeared from Prometheus target discovery\.*
            Status: Firing 🔥
            KubeScheduler has disappeared from Prometheus target discovery\.
            [Runbook URL](https://github\.com/kubernetes\-monitoring/kubernetes\-mixin/tree/master/runbook\.md\#alert\-name\-kubeschedulerdown)

            Alert Name: KubeSchedulerDown
            Severity: critical ❗️
        """.trimIndent())
    }
}