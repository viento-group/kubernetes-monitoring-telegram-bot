package uz.viento.monitoring.prometheus

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uz.viento.monitoring.prometheus.model.PrometheusData
import uz.viento.monitoring.prometheus.service.PrometheusService

@RestController
class PrometheusController(private val prometheusService: PrometheusService) {
    companion object {
        private val logger = LoggerFactory.getLogger(PrometheusController::class.java)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/prometheus/{chatIds}")
    fun onPrometheusMessage(
        @RequestBody data: PrometheusData,
        @PathVariable chatIds: Set<String>,
        @RequestParam("format", required = false) format: String?
    ) {
        logger.debug("Received new message from Prometheus AlertBot for chats $chatIds: $data")
        prometheusService.sendPrometheusAlert(data, chatIds, format)
    }
}