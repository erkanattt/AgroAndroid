package kz.agrosfera.app.util

import androidx.core.text.HtmlCompat

object DiseaseDescriptionParser {

    private val preventionMarker =
        Regex("Как предотвратить|как предотвратить", RegexOption.IGNORE_CASE)

    fun formatClassId(classId: String): String =
        classId.replace("___", " — ").replace('_', ' ')

    fun splitDescription(html: String): Pair<String, String> {
        val parts = preventionMarker.split(html, limit = 2)
        if (parts.size < 2) {
            val plain = htmlToPlain(html)
            return plain to ""
        }
        return htmlToPlain(parts[0]) to htmlToPlain(parts[1])
    }

    private fun htmlToPlain(html: String): String =
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
}
