package com.companyname.android.di



import com.companyname.android.BuildConfig
import com.companyname.android.domain.network.HomeApiService
import com.companyname.android.presentation.utility.Logger
import com.companyname.android.presentation.utility.PrefKeys
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

/**
 * Created Koin module for Network classes
 */
val networkModule = module {

    single { createOkHttpClient() }

    single { createWebService<HomeApiService>(get(), BuildConfig.SERVER_URL_TRANSFORMERS) }

}

fun createOkHttpClient(): OkHttpClient {

    val cookieManager = CookieManager()
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    val client = OkHttpClient.Builder()
        .connectTimeout(120L, TimeUnit.SECONDS)
        .readTimeout(120L, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor {
            var request = it.request()
            val token = Prefs.getString(PrefKeys.AuthKey, "")
            if (token.isNotEmpty()) {
                request = it.request().newBuilder().addHeader("Authorization", "Bearer ${token}")
                    .build()
            }
            return@addInterceptor it.proceed(request)
        }
        .cookieJar(MyCookieJar())
        .build()
    return client
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val gson = GsonBuilder().setLenient().create()
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(T::class.java)
}