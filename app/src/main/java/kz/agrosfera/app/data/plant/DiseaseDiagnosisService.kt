package kz.agrosfera.app.data.plant

import kz.agrosfera.app.data.remote.AiServerPreferences
import kz.agrosfera.app.data.remote.DiseaseApiClient
import kz.agrosfera.app.domain.plant.DiagnosisResult
import kz.agrosfera.app.domain.plant.DiseaseDiagnosisRepository

class DiseaseDiagnosisService : DiseaseDiagnosisRepository {

    private val serverPrefs = AiServerPreferences()
    private var repository: DiseaseRepositoryImpl = createRepository()

    override suspend fun diagnose(imageBytes: ByteArray, filename: String): DiagnosisResult =
        repository.diagnose(imageBytes, filename)

    private fun createRepository(): DiseaseRepositoryImpl =
        DiseaseRepositoryImpl(DiseaseApiClient(serverPrefs.getBaseUrl()))
}
