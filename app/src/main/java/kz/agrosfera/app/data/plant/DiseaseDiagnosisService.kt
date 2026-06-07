package kz.agrosfera.app.data.plant

import android.content.Context
import kz.agrosfera.app.data.remote.AiServerPreferences
import kz.agrosfera.app.data.remote.DiseaseApiClient
import kz.agrosfera.app.domain.plant.DiagnosisResult
import kz.agrosfera.app.domain.plant.DiseaseDiagnosisRepository

class DiseaseDiagnosisService(
    context: Context,
) : DiseaseDiagnosisRepository {

    private val serverPrefs = AiServerPreferences(context)
    private var repository: DiseaseRepositoryImpl = createRepository()

    fun currentBaseUrl(): String = serverPrefs.getBaseUrl()

    fun updateBaseUrl(url: String) {
        serverPrefs.saveBaseUrl(url)
        repository = createRepository()
    }

    fun pingHealth(): Boolean = DiseaseApiClient(serverPrefs.getBaseUrl()).pingHealth()

    override suspend fun diagnose(imageBytes: ByteArray, filename: String): DiagnosisResult =
        repository.diagnose(imageBytes, filename)

    private fun createRepository(): DiseaseRepositoryImpl =
        DiseaseRepositoryImpl(DiseaseApiClient(serverPrefs.getBaseUrl()))
}
