package com.example.weather

import android.app.Application
import com.example.weather.di.mainModule
import com.example.weather.di.networkModule
import org.koin.core.context.startKoin

class App:Application() {
    override fun onCreate() {
        startKoin {
            modules(mainModule, networkModule)
        }
        super.onCreate()
    }
}