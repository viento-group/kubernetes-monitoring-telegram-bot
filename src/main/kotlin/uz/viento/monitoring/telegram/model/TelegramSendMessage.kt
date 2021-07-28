package uz.viento.monitoring.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramSendMessage(
    @JsonProperty("chat_id") val chatId: String,
    @JsonProperty("text") val text: String,
    @JsonProperty("parse_mode") val parseMode: ParseMode?
) {
    companion object {
        enum class ParseMode {
            @JsonProperty("Markdown") MARKDOWN,
            @JsonProperty("MarkdownV2") MARKDOWN_V2,
            @JsonProperty("HTML") HTLM
        }
    }
}