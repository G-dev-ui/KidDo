package com.example.kiddo.di

import com.example.kiddo.data.FirebaseAuthDataSource
import com.example.kiddo.domain.api.AuthDataSource
import org.koin.dsl.module


val repositoryModule = module{
    single<AuthDataSource> { FirebaseAuthDataSource() }
}