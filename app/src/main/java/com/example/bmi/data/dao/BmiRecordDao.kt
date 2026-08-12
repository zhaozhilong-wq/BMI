package com.example.bmi.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiRecordDao {
    @Query("SELECT COUNT(*) FROM bmi_records")
    fun getCount(): Flow<Int>
}