package kz.agrosfera.app.domain.plant

class PredictDiseaseUseCase(
    private val repository: DiseaseDiagnosisRepository,
) {
    suspend fun predict(imageBytes: ByteArray, filename: String = "leaf.jpg"): DiagnosisResult =
        repository.diagnose(imageBytes, filename)
}
