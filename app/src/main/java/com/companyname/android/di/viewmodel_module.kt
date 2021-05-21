package com.companyname.android.di



import com.companyname.android.presentation.splash.SplashViewModel
import com.companyname.android.presentation.transformers.TransformerViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created Koin module for ViewModel class
 */
val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { TransformerViewModel(get()) }
}