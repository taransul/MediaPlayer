package com.example.mediaplayer

import android.app.Application
import com.example.mediaplayer.di.module.appModule
import com.example.mediaplayer.di.module.databaseModule
import com.example.mediaplayer.di.module.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                appModule,
                databaseModule,
                mainModule
            )
        }
    }
}