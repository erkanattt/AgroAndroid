package kz.agrosfera.app.data.local

import kz.agrosfera.app.data.local.dao.DiagnosisDao
import kz.agrosfera.app.data.local.entity.DiagnosisEntity
import kz.agrosfera.app.domain.diagnosis.DiagnosisRecord
import kz.agrosfera.app.domain.diagnosis.DiagnosisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiagnosisRepositoryImpl(
    private val dao: DiagnosisDao,
) : DiagnosisRepository {

    override fun observeRecent(limit: Int): Flow<List<DiagnosisRecord>> =
        dao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getRecent(limit: Int): List<DiagnosisRecord> =
        dao.getRecent(limit).map { it.toDomain() }

    override suspend fun save(
        displayName: String,
        classId: String,
        confidencePercent: Int?,
        symptoms: String,
        prevention: String,
        userEmail: String?,
    ): Long = dao.insert(
        DiagnosisEntity(
            displayName = displayName,
            classId = classId,
            confidencePercent = confidencePercent,
            symptoms = symptoms,
            prevention = prevention,
            isHealthy = classId.contains("healthy", ignoreCase = true),
            userEmail = userEmail,
            timestampMs = System.currentTimeMillis(),
        ),
    )

    private fun DiagnosisEntity.toDomain() = DiagnosisRecord(
        id = id,
        displayName = displayName,
        classId = classId,
        confidencePercent = confidencePercent,
        symptoms = symptoms,
        prevention = prevention,
        isHealthy = isHealthy,
        timestampMs = timestampMs,
    )
}
