package uz.viento.monitoring.prometheus

import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.getAlertName
import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.getEmoji
import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.getParsedString
import uz.viento.monitoring.prometheus.PrometheusDataTextConverterUtils.getSummary
import uz.viento.monitoring.prometheus.model.PrometheusAlert
import uz.viento.monitoring.prometheus.model.PrometheusAlertStatus
import uz.viento.monitoring.prometheus.model.PrometheusData
import uz.viento.monitoring.telegram.TelegramTextUtils

interface PrometheusDataTextConverter {
    fun convert(data: PrometheusData): String
}

object DefaultPrometheusDataTextConverter : PrometheusDataTextConverter {
    override fun convert(data: PrometheusData) = buildString {
        append("*${PrometheusDataTextConverterUtils.getAlertStatusMessage(data.status)}*\n")
        append("\n")
        append("*Active Alert List:*\n")
        data.alerts.map { buildString {
            it.annotations?.let { annotations ->
                it.getSummary()?.also { append("*$it*\n") }
                append(PrometheusDataTextConverterUtils.getAlertStatusMessage(it.status) + "\n")
                annotations.getParsedString("description")?.also { append("$it\n") }
                annotations.getParsedString("runbook_url")?.also { append("[Runbook URL]($it)\n") }
            }

            it.labels?.let { labels -> buildString {
                it.getAlertName()?.also { append("Alert Name: $it\n") }
                labels.getParsedString("severity")
                    ?.also {
                        val emoji = PrometheusDataTextConverterUtils.getSeverityEmoji(it)
                            ?.let { emoji -> " $emoji" } ?: ""
                        append("Severity: $it$emoji\n")
                    }
            } }?.also { if (it.isNotEmpty()) append("\n$it") }
        } }.also { append(it.joinToString(PrometheusDataTextConverterUtils.ALERTS_SEPARATOR).trim()) }
    }
}

object SimplePrometheusDataTextConverter : PrometheusDataTextConverter {
    override fun convert(data: PrometheusData) = buildString {
        append("*${PrometheusDataTextConverterUtils.getAlertStatusMessage(data.status)}*\n\n")
        data.alerts.map { "${it.status.getEmoji()} ${it.getAlertName()}" }
            .joinToString("\n").also { append(it) }
    }
}

object SimpleSummaryListPrometheusDataTextConverter : PrometheusDataTextConverter {
    override fun convert(data: PrometheusData) = buildString {
        append("*${PrometheusDataTextConverterUtils.getAlertStatusMessage(data.status)}*\n\n")
        data.alerts.map { "${it.status.getEmoji()} ${it.getSummary() ?: it.getAlertName()}" }
            .joinToString("\n").also { append(it) }
    }
}

object DetailedPrometheusDataTextConverter : PrometheusDataTextConverter {
    override fun convert(data: PrometheusData) = buildString {
        append("*${PrometheusDataTextConverterUtils.getAlertStatusMessage(data.status)}*\n")
        append("\n")
        append("*Active Alert List:*\n")
        data.alerts.map { buildString {
            append("*${PrometheusDataTextConverterUtils.getAlertStatusMessage(it.status)}*\n")
            it.annotations?.let { annotations -> buildString {
                append("*Annotations:*\n")
                annotations.forEach { (key, value) ->
                    append("*${TelegramTextUtils.escapeSpecialCharacters(key.toString())}*: " +
                            TelegramTextUtils.escapeSpecialCharacters(value.toString()) + "\n") }
            } }?.also { append("$it\n") }

            it.labels?.let { labels -> buildString {
                append("*Labels:*\n")
                labels.forEach { (key, value) ->
                    append("*${TelegramTextUtils.escapeSpecialCharacters(key.toString())}*: " +
                            TelegramTextUtils.escapeSpecialCharacters(value.toString()) + "\n") }
            } }?.also { append("$it\n") }
        } }.also { append(it.joinToString(PrometheusDataTextConverterUtils.ALERTS_SEPARATOR).trim()) }
    }
}

private object PrometheusDataTextConverterUtils {
    val ALERTS_SEPARATOR = TelegramTextUtils.escapeSpecialCharacters("-------------\n")

    fun PrometheusAlertStatus.getEmoji() = when (this) {
        PrometheusAlertStatus.RESOLVED -> "✅"
        PrometheusAlertStatus.FIRING -> "\uD83D\uDD25"
    }

    fun PrometheusAlertStatus.asAlertText() = when (this) {
        PrometheusAlertStatus.RESOLVED -> "Resolved"
        PrometheusAlertStatus.FIRING -> "Firing"
    }.let { "$it ${getEmoji()}" }

    fun getSeverityEmoji(severity: String) = when (severity) {
        "warning" -> "⚠️"
        "critical" -> "❗️"
        "info" -> "ℹ️"
        else -> null
    }

    fun Map<Any, Any>.getParsedString(key: String) =
        get(key)?.let { if (it is String) TelegramTextUtils.escapeSpecialCharacters(it) else null }

    fun getAlertStatusMessage(status: PrometheusAlertStatus) = "Status: ${status.asAlertText()}"

    fun PrometheusAlert.getAlertName() = labels?.getParsedString("alertname")
    fun PrometheusAlert.getSummary() = annotations?.getParsedString("summary")
}