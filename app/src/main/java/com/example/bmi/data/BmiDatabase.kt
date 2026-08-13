package com.example.bmi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bmi.data.dao.BmiRecordDao
import com.example.bmi.data.entity.BmiRecord

@Database(
    entities = [BmiRecord::class],
    version = 1,
    exportSchema = false
)
abstract class BmiDatabase : RoomDatabase() {

    abstract fun bmiRecordDao(): BmiRecordDao

    companion object {
        @Volatile
        private var INSTANCE: BmiDatabase? = null
        fun getDatabase(context: Context): BmiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BmiDatabase::class.java,
                    "bmi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}