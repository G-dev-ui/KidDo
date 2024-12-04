package com.example.kiddo.di



import com.example.kiddo.presentation.AccountSwitchingViewModel
import com.example.kiddo.presentation.AuthViewModel
import com.example.kiddo.presentation.FamilyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { AccountSwitchingViewModel(get(), get(), get(),get(), get()) }
    viewModel { FamilyViewModel(get()) }
}