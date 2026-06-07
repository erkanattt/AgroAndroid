package kz.agrosfera.app.domain.plant

data class PlantDisease(
    val id: String,
    val categoryId: String,
    val name: String,
    val summary: String,
    val symptoms: String,
    val prevention: String,
    val frequencyRank: Int,
)
