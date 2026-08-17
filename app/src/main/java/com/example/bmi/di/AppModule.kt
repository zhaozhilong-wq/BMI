package com.example.bmi.di

import androidx.room.Room
import com.example.bmi.data.BmiDatabase
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.input.InputViewModel
import com.example.bmi.ui.main.MainViewModel
import com.example.bmi.ui.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            BmiDatabase::class.java,
            "bmi_database"
        ).build()
    }

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
    viewModel {
        InputViewModel(
            repository = get()
        )
    }
    viewModel {
        SplashViewModel(
            repository = get()
        )
    }
}