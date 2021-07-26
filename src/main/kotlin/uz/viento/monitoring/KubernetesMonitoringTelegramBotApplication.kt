package uz.viento.monitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KubernetesMonitoringTelegramBotApplication

fun main(args: Array<String>) {
	runApplication<KubernetesMonitoringTelegramBotApplication>(*args)
}
