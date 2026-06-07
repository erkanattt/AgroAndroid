package kz.agrosfera.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class LastDiagnosis(
    val displayName: String,
    val classId: String,
    val confidencePercent: Int?,
    val isHealthy: Boolean,
    val symptoms: String,
    val prevention: String,
    val timestampMs: Long,
)

class DiagnosisHistoryStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(
        name: String,
        confidence: Int?,
        classId: String,
        symptoms: String,
        prevention: String,
    ) {
        val entry = JSONObject()
            .put("name", name)
            .put("classId", classId)
            .put("confidence", confidence ?: -1)
            .put("symptoms", symptoms)
            .put("prevention", prevention)
            .put("time", System.currentTimeMillis())

        val arr = JSONArray(prefs.getString(KEY_HISTORY, "[]"))
        val next = JSONArray().put(entry)
        val limit = minOf(arr.length(), MAX - 1)
        for (i in 0 until limit) {
            next.put(arr.getJSONObject(i))
        }
        prefs.edit().putString(KEY_HISTORY, next.toString()).apply()
    }

    fun getLast(): LastDiagnosis? = getRecent(1).firstOrNull()

    fun getRecent(limit: Int = 5): List<LastDiagnosis> {
        val arr = JSONArray(prefs.getString(KEY_HISTORY, "[]"))
        val result = mutableListOf<LastDiagnosis>()
        for (i in 0 until minOf(arr.length(), limit)) {
            parse(arr.getJSONObject(i))?.let { result.add(it) }
        }
        return result
    }

    private fun parse(obj: JSONObject): LastDiagnosis? {
        val time = obj.optLong("time", 0L)
        if (time == 0L) return null
        val classId = obj.optString("classId", "")
        val conf = obj.optInt("confidence", -1)
        return LastDiagnosis(
            displayName = obj.optString("name"),
            classId = classId,
            confidencePercent = conf.takeIf { it >= 0 },
            isHealthy = classId.contains("healthy", ignoreCase = true),
            symptoms = obj.optString("symptoms"),
            prevention = obj.optString("prevention"),
            timestampMs = time,
        )
    }

    companion object {
        private const val PREFS = "agro_diagnosis_history"
        private const val KEY_HISTORY = "history_json"
        private const val MAX = 20
    }
}
