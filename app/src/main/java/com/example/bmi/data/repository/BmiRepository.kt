package com.example.bmi.data.repository

import com.example.bmi.data.dao.BmiRecordDao
import com.example.bmi.data.entity.BmiRecord

class BmiRepository(
    private val bmiRecordDao: BmiRecordDao
) {

    fun getCount() =
        bmiRecordDao.getCount()

    suspend fun hasRecords(): Boolean {
        return bmiRecordDao.getCountOnce() > 0
    }

    fun getAllRecords() =
        bmiRecordDao.getAllRecords()

    fun getLatestRecord() =
        bmiRecordDao.getLatestRecord()

    suspend fun getById(id: Long) =
        bmiRecordDao.getById(id)

    suspend fun insert(record: BmiRecord): Long {
        return bmiRecordDao.insert(record)
    }

    suspend fun delete(record: BmiRecord) {
        bmiRecordDao.delete(record)
    }

}