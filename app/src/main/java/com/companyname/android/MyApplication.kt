package com.companyname.android



import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import com.companyname.android.di.*
import com.google.firebase.FirebaseApp
import com.pixplicity.easyprefs.library.Prefs
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.android.startKoin

open class MyApplication : Application() {

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()

        init()
    }

    private fun init() {

        context = this

        startKoin(
            this, arrayListOf(
                networkModule,
                repositoryModule,
                viewModelModule,
                roomModule
            )
        )

        FirebaseApp.initializeApp(this)

        JodaTimeAndroid.init(this)

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        /*if(BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this)
        }*/

    }
}