package com.companyname.android.di



import androidx.room.Room
import com.companyname.android.data.database.AppDatabase
import com.companyname.android.presentation.utility.AppConstant
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module


val roomModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            AppConstant.DB_NAME
        ).build()
    }

    single { get<AppDatabase>().appDao() }

}