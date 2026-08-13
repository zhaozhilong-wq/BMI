package com.example.bmi.di

import com.example.bmi.data.BmiDatabase
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        BmiDatabase.getDatabase(androidContext())
    }

    single {
        get<BmiDatabase>().bmiRecordDao()
    }

    single {
        BmiRepository(
            bmiRecordDao = get()
        )
    }

    viewModel {
        MainViewModel(
            repository = get()
        )
    }
}