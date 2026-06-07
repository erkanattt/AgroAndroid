package kz.agrosfera.app.data.plant

import kz.agrosfera.app.data.remote.DiseaseApiClient
import kz.agrosfera.app.data.remote.DiseaseApiException
import kz.agrosfera.app.domain.plant.DiagnosisResult
import kz.agrosfera.app.domain.plant.DiseaseDiagnosisRepository
import kz.agrosfera.app.util.DiseaseDescriptionParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiseaseRepositoryImpl(
    private val apiClient: DiseaseApiClient,
) : DiseaseDiagnosisRepository {

    override suspend fun diagnose(imageBytes: ByteArray, filename: String): DiagnosisResult =
        withContext(Dispatchers.IO) {
            if (imageBytes.isEmpty()) {
                throw DiseaseApiException("empty_image")
            }
            try {
                val response = apiClient.diagnose(imageBytes, filename)
                val (symptoms, prevention) =
                    DiseaseDescriptionParser.splitDescription(response.descriptionHtml)
                DiagnosisResult(
                    classId = response.classId,
                    displayName = DiseaseDescriptionParser.formatClassId(response.classId),
                    symptoms = symptoms.ifBlank { "—" },
                    prevention = prevention.ifBlank { symptoms },
                    confidencePercent = response.confidencePercent,
                )
            } catch (e: DiseaseApiException) {
                throw e
            } catch (e: java.net.ConnectException) {
                throw DiseaseApiException("server_unreachable")
            } catch (e: java.net.SocketTimeoutException) {
                throw DiseaseApiException("server_timeout")
            }
        }
}
