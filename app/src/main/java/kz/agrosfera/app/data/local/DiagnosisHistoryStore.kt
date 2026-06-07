package kz.agrosfera.app.data.local

import android.content.Context

data class LastDiagnosis(
    val displayName: String,
    val confidencePercent: Int?,
    val isHealthy: Boolean,
    val timestampMs: Long,
)

class DiagnosisHistoryStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(name: String, confidence: Int?, classId: String) {
        prefs.edit()
            .putString(KEY_NAME, name)
            .putInt(KEY_CONFIDENCE, confidence ?: -1)
            .putString(KEY_CLASS_ID, classId)
            .putLong(KEY_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getLast(): LastDiagnosis? {
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val conf = prefs.getInt(KEY_CONFIDENCE, -1)
        val classId = prefs.getString(KEY_CLASS_ID, "") ?: ""
        val time = prefs.getLong(KEY_TIME, 0L)
        if (time == 0L) return null
        return LastDiagnosis(
            displayName = name,
            confidencePercent = conf.takeIf { it >= 0 },
            isHealthy = classId.contains("healthy", ignoreCase = true),
            timestampMs = time,
        )
    }

    companion object {
        private const val PREFS = "agro_diagnosis_history"
        private const val KEY_NAME = "name"
        private const val KEY_CONFIDENCE = "confidence"
        private const val KEY_CLASS_ID = "class_id"
        private const val KEY_TIME = "time"
    }
}
