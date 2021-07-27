package uz.viento.monitoring.telegram.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class TelegramConfig {
    @Bean
    fun restTemplate() = RestTemplate()
}