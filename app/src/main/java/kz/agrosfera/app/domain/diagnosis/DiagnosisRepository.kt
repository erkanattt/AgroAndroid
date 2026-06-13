package kz.agrosfera.app.domain.diagnosis

import kotlinx.coroutines.flow.Flow

interface DiagnosisRepository {
    fun observeRecent(limit: Int = 10): Flow<List<DiagnosisRecord>>

    suspend fun getRecent(limit: Int = 10): List<DiagnosisRecord>

    suspend fun save(
        displayName: String,
        classId: String,
        confidencePercent: Int?,
        symptoms: String,
        prevention: String,
        userEmail: String?,
    ): Long
}
