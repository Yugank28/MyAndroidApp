package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChildProfile
import com.example.data.model.HealthRecord
import com.example.data.model.VaccinationRecord
import com.example.data.parser.SampleDataGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChildProfile::class,
        HealthRecord::class,
        VaccinationRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun childDao(): ChildDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "poshan_tracker.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.childDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: ChildDao) {
            if (dao.getChildCount() == 0) {
                dao.upsertProfiles(SampleDataGenerator.getInitialProfiles())
                dao.insertHealthRecords(SampleDataGenerator.getInitialHealthRecords())
                dao.insertVaccinations(SampleDataGenerator.getInitialVaccinations())
            }
        }
    }
}
