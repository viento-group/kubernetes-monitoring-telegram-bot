package uz.viento.monitoring.telegram.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("telegram")
data class TelegramProperties(
    val botToken: String?,
    val kubewatchBotToken: String?
)