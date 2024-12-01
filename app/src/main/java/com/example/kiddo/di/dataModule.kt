package com.example.kiddo.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    }
}