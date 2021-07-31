package uz.viento.monitoring.prometheus.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.prometheus.model.PrometheusData

@Service
class PrometheusServiceImpl : PrometheusService {
    override fun sendPrometheusAlert(data: PrometheusData, chatIds: Set<String>) {
        TODO("Not yet implemented")
    }
}