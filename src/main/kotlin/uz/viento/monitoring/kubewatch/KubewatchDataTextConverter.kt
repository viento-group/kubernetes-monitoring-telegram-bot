package uz.viento.monitoring.kubewatch

import uz.viento.monitoring.kubewatch.model.KubewatchData

interface KubewatchDataTextConverter {
    fun convert(data: KubewatchData): String
}

object SimpleKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = data.text
}

object BoldKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = data.text.replace("`", "*")
}

object DetailedKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = buildString {
        append("*New Kubewatch status:*\n")
        data.eventMeta.map { "${it.key}: *${it.value}*" }
            .joinToString("\n")
            .also { append(it) }
    }
}