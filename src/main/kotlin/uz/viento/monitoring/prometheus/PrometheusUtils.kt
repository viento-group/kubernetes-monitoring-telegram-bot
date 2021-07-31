package uz.viento.monitoring.prometheus

import uz.viento.monitoring.prometheus.model.PrometheusAlertStatus
import uz.viento.monitoring.prometheus.model.PrometheusData

object PrometheusUtils {
    private val DATA_FILTERS = mapOf<String, (d: PrometheusData) -> PrometheusData>(
        "firingOnly" to { it.copy(alerts = it.alerts.filter { it.status == PrometheusAlertStatus.FIRING }) },
        "resolvedOnly" to { it.copy(alerts = it.alerts.filter { it.status == PrometheusAlertStatus.RESOLVED }) },
        "withoutAlerts" to { it.copy(alerts = emptyList()) }
    )

    fun applyFilters(data: PrometheusData, filters: Set<String>) =
        filters.fold(data) { acc, filter -> DATA_FILTERS[filter]?.invoke(acc) ?: acc }
}