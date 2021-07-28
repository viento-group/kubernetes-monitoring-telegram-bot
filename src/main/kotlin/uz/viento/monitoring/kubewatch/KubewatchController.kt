package uz.viento.monitoring.kubewatch

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.kubewatch.service.KubewatchService

@RestController
class KubewatchController(private val kubewatchService: KubewatchService) {
    private companion object {
        private val logger = LoggerFactory.getLogger(KubewatchController::class.java)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/kubewatch/{chatIds}")
    fun onKubewatchAction(
        @RequestBody data: KubewatchData,
        @PathVariable chatIds: Set<String>,
        @RequestParam("format", required = false) format: String?
    ) {
        logger.debug("Sending kubewatch action to chats $chatIds: $data")
        kubewatchService.sendKubewatchStatus(chatIds, data, format)
    }
}