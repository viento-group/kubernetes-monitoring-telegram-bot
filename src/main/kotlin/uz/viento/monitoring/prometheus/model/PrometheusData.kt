package uz.viento.monitoring.prometheus.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class PrometheusData(
    val version: String,
    val groupKey: String?,
    val truncatedAlerts: Int,
    val status: PrometheusAlertStatus,
    val receiver: String,
    val groupLabels: Map<Any, Any>,
    val commonLabels: Map<Any, Any>,
    val commonAnnotations: Map<Any, Any>,
    val externalURL: String?,
    val alerts: List<PrometheusAlert>
)

data class PrometheusAlert(
    val status: PrometheusAlertStatus,
    val labels: Map<Any, Any>?,
    val annotations: Map<Any, Any>?,
    val startsAt: OffsetDateTime,
    val endsAt: OffsetDateTime?,
    val generatorURL: String?,
    val fingerprint: String?
)

enum class PrometheusAlertStatus {
    @JsonProperty("resolved") RESOLVED,
    @JsonProperty("firing") FIRING
}
