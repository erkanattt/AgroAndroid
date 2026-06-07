package kz.agrosfera.app.domain.plant

interface DiseaseDiagnosisRepository {
    suspend fun diagnose(imageBytes: ByteArray, filename: String): DiagnosisResult
}
