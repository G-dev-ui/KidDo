package com.example.kiddo.di


import com.example.kiddo.domain.AuthUseCase
import com.example.kiddo.domain.CreateChildAccountUseCase
import com.example.kiddo.domain.CreateChildAccountUseCaseImpl
import com.example.kiddo.domain.CreateTaskUseCase
import com.example.kiddo.domain.GetFamilyMembersUseCase
import com.example.kiddo.domain.GetFamilyUseCase
import com.example.kiddo.domain.GetTasksUseCase
import com.example.kiddo.domain.GetUserUseCase
import org.koin.dsl.module

val interactorModule = module {

    single {AuthUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory<CreateChildAccountUseCase> { CreateChildAccountUseCaseImpl(get(), get()) }
    factory { GetFamilyMembersUseCase(get()) }
    factory { GetFamilyUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { CreateTaskUseCase(get()) }

}