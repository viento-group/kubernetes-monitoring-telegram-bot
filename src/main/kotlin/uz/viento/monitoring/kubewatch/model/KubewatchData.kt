package uz.viento.monitoring.kubewatch.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class KubewatchData(
    @JsonProperty("eventmeta") val eventMeta: Map<String, Any>,
    val text: String,
    val time: OffsetDateTime
)
