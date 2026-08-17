package com.example.bmi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_records")
data class BmiRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val weightKg: Double,
    val heightCm: Double,

    // 保存用户当时选择的显示单位
    val weightUnit: String,
    val heightUnit: String,

    val bmi: Double,
    val age: Int,
    val gender: String,
    val isChild: Boolean,
    val year: Int,
    val month: Int,
    val day: Int,
    val time: Int
)
