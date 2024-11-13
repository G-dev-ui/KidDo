package com.example.kiddo.myapp

import android.app.Application
import com.example.kiddo.di.dataModule
import com.example.kiddo.di.interactorModule
import com.example.kiddo.di.repositoryModule
import com.example.kiddo.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация Koin
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(dataModule, repositoryModule, interactorModule, viewModelModule))  // Укажите ваши модули
        }
    }
}