package com.companyname.android.di



import com.companyname.android.data.contract.HomeRepo
import com.companyname.android.data.repository.HomeRepository
import org.koin.dsl.module.module

/**
 * Created Koin module for Repository class
 */

val repositoryModule = module {
    single<HomeRepo> { HomeRepository(get(), get()) }
}