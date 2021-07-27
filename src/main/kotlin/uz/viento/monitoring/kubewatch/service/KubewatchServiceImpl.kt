package uz.viento.monitoring.kubewatch.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.telegram.BotType
import uz.viento.monitoring.telegram.service.TelegramService

@Service
class KubewatchServiceImpl(private val telegramService: TelegramService) : KubewatchService {
    override fun sendKubewatchStatus(chatIds: Set<String>, data: KubewatchData) {
        // TODO add text converters
        telegramService.sendMessages(data.text, chatIds, BotType.KUBEWATCH)
    }
}