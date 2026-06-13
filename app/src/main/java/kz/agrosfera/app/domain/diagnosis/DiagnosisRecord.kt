package kz.agrosfera.app.domain.diagnosis

data class DiagnosisRecord(
    val id: Long,
    val displayName: String,
    val classId: String,
    val confidencePercent: Int?,
    val symptoms: String,
    val prevention: String,
    val isHealthy: Boolean,
    val timestampMs: Long,
)
