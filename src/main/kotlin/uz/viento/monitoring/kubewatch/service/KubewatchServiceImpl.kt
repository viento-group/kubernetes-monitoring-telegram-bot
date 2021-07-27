package uz.viento.monitoring.kubewatch.service

import org.springframework.stereotype.Service
import uz.viento.monitoring.kubewatch.model.KubewatchData

@Service
class KubewatchServiceImpl : KubewatchService {
    override fun sendKubewatchStatus(chatIds: Set<Long>, data: KubewatchData) {
        TODO("Not yet implemented")
    }
}