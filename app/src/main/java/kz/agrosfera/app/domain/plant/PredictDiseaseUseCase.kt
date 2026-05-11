package kz.agrosfera.app.domain.plant

class PredictDiseaseUseCase(
    private val diseases: List<PlantDisease> = PlantDiseaseCatalog.diseases,
) {
    fun predict(seed: Long): DiagnosisResult {
        if (diseases.isEmpty()) {
            throw IllegalStateException("Empty catalog")
        }
        val size = diseases.size
        val idx = ((seed % size + size) % size).toInt()
        val disease = diseases[idx]
        val confidence = 84 + (kotlin.math.abs(seed.toInt()) % 15)
        return DiagnosisResult(disease, confidence)
    }
}
