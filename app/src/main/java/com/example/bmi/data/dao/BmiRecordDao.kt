package com.example.bmi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bmi.data.entity.BmiRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiRecordDao {
    @Query("SELECT COUNT(*) FROM bmi_records")
    fun getCount(): Flow<Int>

    // 点击 Calculate 时，瞬间判断当前是不是新用户
    @Query("SELECT COUNT(*) FROM bmi_records")
    suspend fun getCountOnce(): Int

    @Insert
    suspend fun insert(record: BmiRecord): Long

    @Query("""
    SELECT * FROM bmi_records
    ORDER BY 
        year DESC,
        month DESC,
        day DESC,
        time DESC,
        createdAt DESC""")
    fun getAllRecords(): Flow<List<BmiRecord>>

    @Query("""
        SELECT * FROM bmi_records
        ORDER BY year DESC, month DESC, day DESC, time DESC,createdAt DESC
        LIMIT 1
    """)
    fun getLatestRecord(): Flow<BmiRecord?>

    @Query("SELECT * FROM bmi_records WHERE id = :id")
    suspend fun getById(id: Long): BmiRecord?

    @Delete
    suspend fun delete(record: BmiRecord)



}