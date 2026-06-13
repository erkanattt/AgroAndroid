package kz.agrosfera.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kz.agrosfera.app.data.local.dao.DiagnosisDao
import kz.agrosfera.app.data.local.entity.DiagnosisEntity

@Database(
    entities = [DiagnosisEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agrosphere.db",
                ).build().also { instance = it }
            }
    }
}
