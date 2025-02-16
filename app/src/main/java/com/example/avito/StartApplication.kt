package com.example.avito

import android.app.Application
import com.example.avito.di.dataModule
import com.example.avito.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StartApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@StartApplication)
            modules(listOf(presentationModule, dataModule))
        }
    }
}