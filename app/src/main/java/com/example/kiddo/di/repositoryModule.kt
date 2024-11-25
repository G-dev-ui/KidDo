package com.example.kiddo.di

import com.example.kiddo.data.FirebaseAuthDataSource
import com.example.kiddo.data.UserRepositoryImpl
import com.example.kiddo.domain.api.AuthDataSource
import com.example.kiddo.domain.api.UserRepository
import org.koin.dsl.module


val repositoryModule = module{
    single<AuthDataSource> { FirebaseAuthDataSource() }
    single<UserRepository> { UserRepositoryImpl(get()) }
}