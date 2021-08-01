package uz.viento.monitoring.telegram.client

import uz.viento.monitoring.telegram.BotToken
import uz.viento.monitoring.telegram.model.TelegramSendMessage

interface TelegramClient {
    fun sendMessage(sendMessage: TelegramSendMessage, token: BotToken)
}