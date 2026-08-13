package com.example.bmi

import android.app.Application
import com.example.bmi.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BmiApplication : Application() {
    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BmiApplication)
            modules(appModule)
        }
    }
}