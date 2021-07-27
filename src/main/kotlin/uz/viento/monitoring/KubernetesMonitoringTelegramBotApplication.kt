package uz.viento.monitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import uz.viento.monitoring.telegram.properties.TelegramProperties

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties::class)
class KubernetesMonitoringTelegramBotApplication

fun main(args: Array<String>) {
	runApplication<KubernetesMonitoringTelegramBotApplication>(*args)
}
