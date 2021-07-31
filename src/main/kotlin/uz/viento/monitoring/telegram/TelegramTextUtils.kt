package uz.viento.monitoring.telegram

object TelegramTextUtils {
    private const val MAX_MESSAGE_LENGTH = 4096
    val specialCharacters = setOf("_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!")

    fun escapeSpecialCharacters(text: String) = specialCharacters.fold(text) { acc, v -> acc.replace(v, "\\$v") }

    fun splitMessageTextToParts(text: String): List<String> {
        val parts = ArrayList<String>()
        var leftText: String? = text

        while (leftText != null) {
            if (leftText.length > MAX_MESSAGE_LENGTH) {
                parts += leftText.substring(0, MAX_MESSAGE_LENGTH)
                leftText = leftText.substring(MAX_MESSAGE_LENGTH)
            } else {
                parts += leftText
                leftText = null
            }
        }

        return parts
    }
}