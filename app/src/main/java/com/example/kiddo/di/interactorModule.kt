package com.example.kiddo.di


import com.example.kiddo.domain.AuthUseCase
import org.koin.dsl.module

val interactorModule = module {

    single {AuthUseCase(get()) }
}