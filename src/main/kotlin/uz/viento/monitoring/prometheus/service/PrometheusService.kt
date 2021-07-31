package uz.viento.monitoring.prometheus.service

import uz.viento.monitoring.prometheus.model.PrometheusData

interface PrometheusService {
    fun sendPrometheusAlert(data: PrometheusData, chatIds: Set<String>, format: String?)
}