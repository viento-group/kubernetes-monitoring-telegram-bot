package uz.viento.monitoring.kubewatch.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.kubewatch.BoldKubewatchDataTextConverter
import uz.viento.monitoring.kubewatch.DetailedKubewatchDataTextConverter
import uz.viento.monitoring.kubewatch.SimpleKubewatchDataTextConverter
import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService

@Service
class KubewatchServiceImpl(private val telegramService: TelegramService) : KubewatchService {
    override fun sendKubewatchStatus(chatIds: Set<String>, data: KubewatchData, format: String?) {
        val text = convertDataToText(format, data)
        telegramService.sendMessages(text, chatIds, BotType.KUBEWATCH)
    }

    private companion object {
        fun convertDataToText(format: String?, data: KubewatchData) = when (format) {
            "bold" -> BoldKubewatchDataTextConverter.convert(data)
            "detailed" -> DetailedKubewatchDataTextConverter.convert(data)
            else -> SimpleKubewatchDataTextConverter.convert(data)
        }
    }
}