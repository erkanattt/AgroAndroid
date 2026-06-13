package kz.agrosfera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kz.agrosfera.app.data.local.entity.DiagnosisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {

    @Query("SELECT * FROM diagnoses ORDER BY timestampMs DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses ORDER BY timestampMs DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE id = :id")
    suspend fun getById(id: Long): DiagnosisEntity?

    @Insert
    suspend fun insert(entity: DiagnosisEntity): Long
}
