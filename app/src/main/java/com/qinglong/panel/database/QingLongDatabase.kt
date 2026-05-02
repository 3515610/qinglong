package com.qinglong.panel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TaskEntity::class,
        EnvironmentVariableEntity::class,
        ScriptEntity::class,
        LogEntity::class,
        UpdateHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class QingLongDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun environmentVariableDao(): EnvironmentVariableDao
    abstract fun scriptDao(): ScriptDao
    abstract fun logDao(): LogDao
    abstract fun updateHistoryDao(): UpdateHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QingLongDatabase? = null

        fun getInstance(context: Context): QingLongDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QingLongDatabase::class.java,
                    "qinglong_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS update_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "update_time INTEGER NOT NULL, " +
                    "status INTEGER NOT NULL, " +
                    "description TEXT)"
                )
            }
        }
    }
}
