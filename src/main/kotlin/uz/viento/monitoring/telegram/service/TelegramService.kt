package uz.viento.monitoring.telegram.service

import uz.viento.monitoring.telegram.BotType

interface TelegramService {
    fun sendMessages(text: String, chatIds: Set<String>, botType: BotType)
}