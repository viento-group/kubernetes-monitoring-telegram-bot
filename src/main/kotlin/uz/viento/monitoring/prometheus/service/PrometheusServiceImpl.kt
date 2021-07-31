package uz.viento.monitoring.prometheus.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.prometheus.DefaultPrometheusDataTextConverter
import uz.viento.monitoring.prometheus.DetailedPrometheusDataTextConverter
import uz.viento.monitoring.prometheus.SimplePrometheusDataTextConverter
import uz.viento.monitoring.prometheus.SimpleSummaryListPrometheusDataTextConverter
import uz.viento.monitoring.prometheus.model.PrometheusData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService

@Service
class PrometheusServiceImpl(private val telegramService: TelegramService) : PrometheusService {
    override fun sendPrometheusAlert(data: PrometheusData, chatIds: Set<String>, format: String?) {
        val text = convertDataToText(format, data)
        telegramService.sendMessages(text, chatIds, BotType.PROMETHEUS_ALERT_MANAGER)
    }

    companion object {
        fun convertDataToText(format: String?, data: PrometheusData) = when (format) {
            "simple" -> SimplePrometheusDataTextConverter.convert(data)
            "simple_summary" -> SimpleSummaryListPrometheusDataTextConverter.convert(data)
            "detailed" -> DetailedPrometheusDataTextConverter.convert(data)
            else -> DefaultPrometheusDataTextConverter.convert(data)
        }
    }
}