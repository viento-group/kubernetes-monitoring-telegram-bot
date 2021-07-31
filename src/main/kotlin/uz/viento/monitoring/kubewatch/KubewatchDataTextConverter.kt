package uz.viento.monitoring.kubewatch

import uz.viento.monitoring.kubewatch.model.KubewatchData
import uz.viento.monitoring.telegram.TelegramTextUtils

interface KubewatchDataTextConverter {
    fun convert(data: KubewatchData): String
}

object SimpleKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = TelegramTextUtils.escapeSpecialCharacters(data.text)
        .replace("\\`", "`")
}

object BoldKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = TelegramTextUtils.escapeSpecialCharacters(data.text)
        .replace("\\`", "*")
}

object DetailedKubewatchDataTextConverter : KubewatchDataTextConverter {
    override fun convert(data: KubewatchData) = buildString {
        append("*New Kubewatch status:*\n")
        data.eventMeta.map {
            val key = TelegramTextUtils.escapeSpecialCharacters(it.key)
            val value = TelegramTextUtils.escapeSpecialCharacters(it.value)
            "$key: *$value*"
        }.joinToString("\n").also { append(it) }
    }
}