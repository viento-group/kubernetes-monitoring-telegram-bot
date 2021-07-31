package uz.viento.monitoring.prometheus

import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.asAlertText
import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.getParsedString
import uz.viento.monitoring.prometheus.model.PrometheusAlertStatus
import uz.viento.monitoring.prometheus.model.PrometheusData
import uz.viento.monitoring.telegram.TelegramTextUtils

interface PrometheusDataTextConverter {
    fun convert(data: PrometheusData): String
}

object DefaultPrometheusDataTextConverter : PrometheusDataTextConverter {
    private val alertsSeparator = TelegramTextUtils.escapeSpecialCharacters("-------------\n")

    override fun convert(data: PrometheusData) = buildString {
        append("*${getAlertStatusMessage(data.status)}*\n")
        append("\n")
        append("*Active Alert List:*\n")
        data.alerts.map { buildString {
            it.annotations?.let { annotation ->
                annotation.getParsedString("summary")?.also { append("*$it*\n") }
                append(getAlertStatusMessage(it.status) + "\n")
                annotation.getParsedString("description")?.also { append("$it\n") }
                annotation.getParsedString("runbook_url")?.also { append("[Runbook URL]($it)\n") }
            }

            it.labels?.let { labels -> buildString {
                labels.getParsedString("alertname")?.also { append("Alert Name: $it\n") }
                labels.getParsedString("severity")
                    ?.also {
                        val emoji = PrometheusDataTextConverterUtils.getSeverityEmoji(it)
                            ?.let { emoji -> " $emoji" } ?: ""
                        append("Severity: $it$emoji\n")
                    }
            } }?.also { if (it.isNotEmpty()) append("\n$it") }
        } }.also { append(it.joinToString(alertsSeparator).trim()) }
    }

    private fun getAlertStatusMessage(status: PrometheusAlertStatus) = "Status: ${status.asAlertText()}"
}

private object PrometheusDataTextConverterUtils {
    fun PrometheusAlertStatus.asAlertText() = when (this) {
        PrometheusAlertStatus.RESOLVED -> "Resolved ✅"
        PrometheusAlertStatus.FIRING -> "Firing \uD83D\uDD25"
    }

    fun getSeverityEmoji(severity: String) = when (severity) {
        "warning" -> "⚠️"
        "critical" -> "❗️"
        "info" -> "ℹ️"
        else -> null
    }

    fun Map<Any, Any>.getParsedString(key: String) =
        get(key)?.let { if (it is String) TelegramTextUtils.escapeSpecialCharacters(it) else null }
}