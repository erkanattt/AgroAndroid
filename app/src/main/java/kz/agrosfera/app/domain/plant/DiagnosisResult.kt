package kz.agrosfera.app.domain.plant

data class DiagnosisResult(
    val classId: String,
    val displayName: String,
    val symptoms: String,
    val prevention: String,
    val confidencePercent: Int?,
)
