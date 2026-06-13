package kz.agrosfera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val classId: String,
    val confidencePercent: Int?,
    val symptoms: String,
    val prevention: String,
    val isHealthy: Boolean,
    val userEmail: String?,
    val timestampMs: Long,
)
