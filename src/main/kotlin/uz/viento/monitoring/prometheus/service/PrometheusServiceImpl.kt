package uz.viento.monitoring.prometheus.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.prometheus.DefaultPrometheusDataTextConverter
import uz.viento.monitoring.prometheus.model.PrometheusData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService

@Service
class PrometheusServiceImpl(private val telegramService: TelegramService) : PrometheusService {
    override fun sendPrometheusAlert(data: PrometheusData, chatIds: Set<String>) {
        val text = DefaultPrometheusDataTextConverter.convert(data)
        telegramService.sendMessages(text, chatIds, BotType.PROMETHEUS_ALERT_MANAGER)
    }
}