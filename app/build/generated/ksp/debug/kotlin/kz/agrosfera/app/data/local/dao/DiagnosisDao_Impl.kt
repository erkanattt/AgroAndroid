package kz.agrosfera.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kz.agrosfera.app.`data`.local.entity.DiagnosisEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DiagnosisDao_Impl(
  __db: RoomDatabase,
) : DiagnosisDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDiagnosisEntity: EntityInsertAdapter<DiagnosisEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDiagnosisEntity = object : EntityInsertAdapter<DiagnosisEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `diagnoses` (`id`,`displayName`,`classId`,`confidencePercent`,`symptoms`,`prevention`,`isHealthy`,`userEmail`,`timestampMs`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DiagnosisEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.displayName)
        statement.bindText(3, entity.classId)
        val _tmpConfidencePercent: Int? = entity.confidencePercent
        if (_tmpConfidencePercent == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpConfidencePercent.toLong())
        }
        statement.bindText(5, entity.symptoms)
        statement.bindText(6, entity.prevention)
        val _tmp: Int = if (entity.isHealthy) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpUserEmail: String? = entity.userEmail
        if (_tmpUserEmail == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpUserEmail)
        }
        statement.bindLong(9, entity.timestampMs)
      }
    }
  }

  public override suspend fun insert(entity: DiagnosisEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfDiagnosisEntity.insertAndReturnId(_connection, entity)
    _result
  }

  public override fun observeRecent(limit: Int): Flow<List<DiagnosisEntity>> {
    val _sql: String = "SELECT * FROM diagnoses ORDER BY timestampMs DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("diagnoses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "classId")
        val _columnIndexOfConfidencePercent: Int = getColumnIndexOrThrow(_stmt, "confidencePercent")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfPrevention: Int = getColumnIndexOrThrow(_stmt, "prevention")
        val _columnIndexOfIsHealthy: Int = getColumnIndexOrThrow(_stmt, "isHealthy")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfTimestampMs: Int = getColumnIndexOrThrow(_stmt, "timestampMs")
        val _result: MutableList<DiagnosisEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpConfidencePercent: Int?
          if (_stmt.isNull(_columnIndexOfConfidencePercent)) {
            _tmpConfidencePercent = null
          } else {
            _tmpConfidencePercent = _stmt.getLong(_columnIndexOfConfidencePercent).toInt()
          }
          val _tmpSymptoms: String
          _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          val _tmpPrevention: String
          _tmpPrevention = _stmt.getText(_columnIndexOfPrevention)
          val _tmpIsHealthy: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsHealthy).toInt()
          _tmpIsHealthy = _tmp != 0
          val _tmpUserEmail: String?
          if (_stmt.isNull(_columnIndexOfUserEmail)) {
            _tmpUserEmail = null
          } else {
            _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          }
          val _tmpTimestampMs: Long
          _tmpTimestampMs = _stmt.getLong(_columnIndexOfTimestampMs)
          _item =
              DiagnosisEntity(_tmpId,_tmpDisplayName,_tmpClassId,_tmpConfidencePercent,_tmpSymptoms,_tmpPrevention,_tmpIsHealthy,_tmpUserEmail,_tmpTimestampMs)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecent(limit: Int): List<DiagnosisEntity> {
    val _sql: String = "SELECT * FROM diagnoses ORDER BY timestampMs DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "classId")
        val _columnIndexOfConfidencePercent: Int = getColumnIndexOrThrow(_stmt, "confidencePercent")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfPrevention: Int = getColumnIndexOrThrow(_stmt, "prevention")
        val _columnIndexOfIsHealthy: Int = getColumnIndexOrThrow(_stmt, "isHealthy")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfTimestampMs: Int = getColumnIndexOrThrow(_stmt, "timestampMs")
        val _result: MutableList<DiagnosisEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpConfidencePercent: Int?
          if (_stmt.isNull(_columnIndexOfConfidencePercent)) {
            _tmpConfidencePercent = null
          } else {
            _tmpConfidencePercent = _stmt.getLong(_columnIndexOfConfidencePercent).toInt()
          }
          val _tmpSymptoms: String
          _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          val _tmpPrevention: String
          _tmpPrevention = _stmt.getText(_columnIndexOfPrevention)
          val _tmpIsHealthy: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsHealthy).toInt()
          _tmpIsHealthy = _tmp != 0
          val _tmpUserEmail: String?
          if (_stmt.isNull(_columnIndexOfUserEmail)) {
            _tmpUserEmail = null
          } else {
            _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          }
          val _tmpTimestampMs: Long
          _tmpTimestampMs = _stmt.getLong(_columnIndexOfTimestampMs)
          _item =
              DiagnosisEntity(_tmpId,_tmpDisplayName,_tmpClassId,_tmpConfidencePercent,_tmpSymptoms,_tmpPrevention,_tmpIsHealthy,_tmpUserEmail,_tmpTimestampMs)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): DiagnosisEntity? {
    val _sql: String = "SELECT * FROM diagnoses WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "classId")
        val _columnIndexOfConfidencePercent: Int = getColumnIndexOrThrow(_stmt, "confidencePercent")
        val _columnIndexOfSymptoms: Int = getColumnIndexOrThrow(_stmt, "symptoms")
        val _columnIndexOfPrevention: Int = getColumnIndexOrThrow(_stmt, "prevention")
        val _columnIndexOfIsHealthy: Int = getColumnIndexOrThrow(_stmt, "isHealthy")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfTimestampMs: Int = getColumnIndexOrThrow(_stmt, "timestampMs")
        val _result: DiagnosisEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpConfidencePercent: Int?
          if (_stmt.isNull(_columnIndexOfConfidencePercent)) {
            _tmpConfidencePercent = null
          } else {
            _tmpConfidencePercent = _stmt.getLong(_columnIndexOfConfidencePercent).toInt()
          }
          val _tmpSymptoms: String
          _tmpSymptoms = _stmt.getText(_columnIndexOfSymptoms)
          val _tmpPrevention: String
          _tmpPrevention = _stmt.getText(_columnIndexOfPrevention)
          val _tmpIsHealthy: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsHealthy).toInt()
          _tmpIsHealthy = _tmp != 0
          val _tmpUserEmail: String?
          if (_stmt.isNull(_columnIndexOfUserEmail)) {
            _tmpUserEmail = null
          } else {
            _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          }
          val _tmpTimestampMs: Long
          _tmpTimestampMs = _stmt.getLong(_columnIndexOfTimestampMs)
          _result =
              DiagnosisEntity(_tmpId,_tmpDisplayName,_tmpClassId,_tmpConfidencePercent,_tmpSymptoms,_tmpPrevention,_tmpIsHealthy,_tmpUserEmail,_tmpTimestampMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
