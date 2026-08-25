package com.example.bmi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bmi.data.dao.BmiRecordDao
import com.example.bmi.data.entity.BmiRecord

@Database(
    entities = [BmiRecord::class],
    version = 3,
    exportSchema = false
)
abstract class BmiDatabase : RoomDatabase() {

    abstract fun bmiRecordDao(): BmiRecordDao
}