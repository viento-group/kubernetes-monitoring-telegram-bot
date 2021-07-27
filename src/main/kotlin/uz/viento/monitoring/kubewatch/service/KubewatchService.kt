package uz.viento.monitoring.kubewatch.service

import uz.viento.monitoring.kubewatch.model.KubewatchData

interface KubewatchService {
    fun sendKubewatchStatus(chatIds: Set<String>, data: KubewatchData, format: String?)
}