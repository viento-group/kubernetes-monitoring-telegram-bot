package uz.viento.monitoring.prometheus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PrometheusDataTextConverterTest {
    private companion object {
        val alert = PrometheusTestData.PROMETHEUS_DATA.alerts[0]
        val prometheusData = PrometheusTestData.PROMETHEUS_DATA.copy(alerts = listOf(alert, alert))
    }

    @Test
    fun `default converter`() {
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

    @Test
    fun `simple converter`() {
        val text = SimplePrometheusDataTextConverter.convert(prometheusData)

        assertThat(text).isEqualTo("""
            *Status: Firing 🔥*

            🔥 KubeSchedulerDown
            🔥 KubeSchedulerDown
        """.trimIndent())
    }

    @Test
    fun `simple summary converter`() {
        val text = SimpleSummaryListPrometheusDataTextConverter.convert(prometheusData)

        assertThat(text).isEqualTo("""
            *Status: Firing 🔥*

            🔥 Target disappeared from Prometheus target discovery\.
            🔥 Target disappeared from Prometheus target discovery\.
        """.trimIndent())
    }

    @Test
    fun `detailed converter`() {
        val text = DetailedPrometheusDataTextConverter.convert(prometheusData)

        assertThat(text).isEqualTo("""
            *Status: Firing 🔥*

            *Active Alert List:*
            *Status: Firing 🔥*
            *Annotations:*
            *description*: KubeScheduler has disappeared from Prometheus target discovery\.
            *runbook\_url*: https://github\.com/kubernetes\-monitoring/kubernetes\-mixin/tree/master/runbook\.md\#alert\-name\-kubeschedulerdown
            *summary*: Target disappeared from Prometheus target discovery\.
            
            *Labels:*
            *alertname*: KubeSchedulerDown
            *prometheus*: kube\-prometheus\-stack/kube\-prometheus\-stack\-prometheus
            *severity*: critical
            
            \-\-\-\-\-\-\-\-\-\-\-\-\-
            *Status: Firing 🔥*
            *Annotations:*
            *description*: KubeScheduler has disappeared from Prometheus target discovery\.
            *runbook\_url*: https://github\.com/kubernetes\-monitoring/kubernetes\-mixin/tree/master/runbook\.md\#alert\-name\-kubeschedulerdown
            *summary*: Target disappeared from Prometheus target discovery\.
            
            *Labels:*
            *alertname*: KubeSchedulerDown
            *prometheus*: kube\-prometheus\-stack/kube\-prometheus\-stack\-prometheus
            *severity*: critical
        """.trimIndent())
    }
}