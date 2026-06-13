package kz.agrosfera.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass
import kz.agrosfera.app.`data`.local.dao.DiagnosisDao
import kz.agrosfera.app.`data`.local.dao.DiagnosisDao_Impl

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _diagnosisDao: Lazy<DiagnosisDao> = lazy {
    DiagnosisDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "d2cee19b657a95c289722d9ccc215673", "6c6ae6d68e00690e816208a453842085") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `diagnoses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `displayName` TEXT NOT NULL, `classId` TEXT NOT NULL, `confidencePercent` INTEGER, `symptoms` TEXT NOT NULL, `prevention` TEXT NOT NULL, `isHealthy` INTEGER NOT NULL, `userEmail` TEXT, `timestampMs` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd2cee19b657a95c289722d9ccc215673')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `diagnoses`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsDiagnoses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDiagnoses.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("classId", TableInfo.Column("classId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("confidencePercent", TableInfo.Column("confidencePercent", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("symptoms", TableInfo.Column("symptoms", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("prevention", TableInfo.Column("prevention", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("isHealthy", TableInfo.Column("isHealthy", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("userEmail", TableInfo.Column("userEmail", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("timestampMs", TableInfo.Column("timestampMs", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDiagnoses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDiagnoses: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDiagnoses: TableInfo = TableInfo("diagnoses", _columnsDiagnoses,
            _foreignKeysDiagnoses, _indicesDiagnoses)
        val _existingDiagnoses: TableInfo = read(connection, "diagnoses")
        if (!_infoDiagnoses.equals(_existingDiagnoses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |diagnoses(kz.agrosfera.app.data.local.entity.DiagnosisEntity).
              | Expected:
              |""".trimMargin() + _infoDiagnoses + """
              |
              | Found:
              |""".trimMargin() + _existingDiagnoses)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "diagnoses")
  }

  public override fun clearAllTables() {
    super.performClear(false, "diagnoses")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(DiagnosisDao::class, DiagnosisDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun diagnosisDao(): DiagnosisDao = _diagnosisDao.value
}
