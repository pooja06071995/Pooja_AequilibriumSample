package com.companyname.android

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.companyname.android.data.Either
import com.companyname.android.data.contract.HomeRepo
import com.companyname.android.data.database.AppDao
import com.companyname.android.data.database.AppDatabase
import com.companyname.android.data.datasource.HomeLocaDataSource
import com.companyname.android.data.datasource.HomeRemoteDataSource
import com.companyname.android.data.repository.HomeRepository
import com.companyname.android.domain.exceptions.MyException
import com.companyname.android.domain.network.HomeApiService
import com.companyname.android.presentation.transformers.TransformerViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing




open class BaseTest {

    var exceptionLiveData : MutableLiveData<MyException> = MutableLiveData()
    @Mock
    lateinit var homeRepo : HomeRepo
    @Mock
    lateinit var homeLocalDataSource: HomeLocaDataSource
    @Mock
    lateinit var homeRemoteDataSource: HomeRemoteDataSource
    @Mock
    lateinit var homeApiService : HomeApiService

    lateinit var homeRepository: HomeRepository
    lateinit var viewModel : TransformerViewModel

    lateinit var appDatabase : AppDatabase
    lateinit var appDao: AppDao

    @Before
    fun testBeforeSetup(){

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        appDao = appDatabase.appDao()

        homeRepository = HomeRepository(homeApiService, appDao)
        viewModel = TransformerViewModel(homeRepo)

    }

    fun <T> postValue(either: Either<MyException, T>, successLiveData: MutableLiveData<T>) {
        either.either({
            exceptionLiveData.postValue(it)
        }, {
            successLiveData.postValue(it)
        })
    }

    fun <T> verifyBlocking(mock: T, f: suspend T.() -> Unit) {
        val m = Mockito.verify(mock)
        runBlocking { m.f() }
    }

    inline fun <reified T> mock() = Mockito.mock(T::class.java)
    inline fun <T> whenever(methodCall: T) : OngoingStubbing<T> =
        Mockito.`when`(methodCall)
}