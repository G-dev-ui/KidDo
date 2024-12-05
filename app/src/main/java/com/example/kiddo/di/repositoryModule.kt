package com.example.kiddo.di

import com.example.kiddo.data.FamilyRepositoryImpl
import com.example.kiddo.data.FirebaseAuthDataSource
import com.example.kiddo.data.TaskRepositoryImpl
import com.example.kiddo.data.UserRepositoryImpl
import com.example.kiddo.domain.api.AuthDataSource
import com.example.kiddo.domain.api.FamilyRepository
import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.api.UserRepository
import org.koin.dsl.module


val repositoryModule = module{
    single<AuthDataSource> { FirebaseAuthDataSource() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<FamilyRepository> { FamilyRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
}