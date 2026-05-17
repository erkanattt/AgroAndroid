package kz.agrosfera.app.domain.plant

data class PlantDisease(
    val id: String,
    val name: String,
    val summary: String,
    val symptoms: String,
    val prevention: String,
    val frequencyRank: Int,
)

data class DiagnosisResult(
    val classId: String,
    val displayName: String,
    val symptoms: String,
    val prevention: String,
    val confidencePercent: Int?,
)
