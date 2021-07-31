package uz.viento.monitoring.prometheus

import uz.viento.monitoring.prometheus.model.PrometheusAlert
import uz.viento.monitoring.prometheus.model.PrometheusAlertStatus
import uz.viento.monitoring.prometheus.model.PrometheusData
import java.time.OffsetDateTime
import java.time.ZoneOffset

object PrometheusTestData {
    // this json taken from real Prometheus AlertManager message
    const val PROMETHEUS_ALERT_JSON = """
        {
            "receiver": "telegram-bot",
            "status": "firing",
            "alerts": [
                {
                  "status": "firing",
                  "labels": {
                    "alertname": "KubeSchedulerDown",
                    "prometheus": "kube-prometheus-stack/kube-prometheus-stack-prometheus",
                    "severity": "critical"
                  },
                  "annotations": {
                    "description": "KubeScheduler has disappeared from Prometheus target discovery.",
                    "runbook_url": "https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubeschedulerdown",
                    "summary": "Target disappeared from Prometheus target discovery."
                  },
                  "startsAt": "2021-07-31T12:15:22.849Z",
                  "endsAt": "0001-01-01T00:00:00Z",
                  "generatorURL": "http://kube-prometheus-stack-prometheus.kube-prometheus-stack:9090/graph?g0.expr=absent%28up%7Bjob%3D%22kube-scheduler%22%7D+%3D%3D+1%29&g0.tab=1",
                  "fingerprint": "c56e29efa93aa0c6"
                }
            ],
            "groupLabels": {},
            "commonLabels": {},
            "commonAnnotations": {},
            "externalURL": "http://kube-prometheus-stack-alertmanager.kube-prometheus-stack:9093",
            "version": "4",
            "groupKey": "{}:{}",
            "truncatedAlerts": 0
        }
    """

    val PROMETHEUS_DATA = PrometheusData(
        version = "4",
        groupKey = "{}:{}",
        truncatedAlerts = 0,
        status = PrometheusAlertStatus.FIRING,
        receiver = "telegram-bot",
        groupLabels = emptyMap(),
        commonLabels = emptyMap(),
        commonAnnotations = emptyMap(),
        externalURL = "http://kube-prometheus-stack-alertmanager.kube-prometheus-stack:9093",
        alerts = listOf(
            PrometheusAlert(
                status = PrometheusAlertStatus.FIRING,
                labels = mapOf(
                    "alertname" to "KubeSchedulerDown",
                    "prometheus" to "kube-prometheus-stack/kube-prometheus-stack-prometheus",
                    "severity" to "critical"
                ),
                annotations = mapOf(
                    "description" to "KubeScheduler has disappeared from Prometheus target discovery.",
                    "runbook_url" to "https://github.com/kubernetes-monitoring/kubernetes-mixin/tree/master/runbook.md#alert-name-kubeschedulerdown",
                    "summary" to "Target disappeared from Prometheus target discovery."
                ),
                startsAt = OffsetDateTime.parse("2021-07-31T12:15:22.849Z"),
                endsAt = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                generatorURL = "http://kube-prometheus-stack-prometheus.kube-prometheus-stack:9090/graph?g0.expr=absent%28up%7Bjob%3D%22kube-scheduler%22%7D+%3D%3D+1%29&g0.tab=1",
                fingerprint = "c56e29efa93aa0c6"
            )
        )
    )
}