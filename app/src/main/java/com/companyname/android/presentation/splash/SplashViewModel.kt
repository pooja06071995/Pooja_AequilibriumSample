package com.companyname.android.presentation.splash



import androidx.lifecycle.MutableLiveData
import com.companyname.android.data.contract.HomeRepo
import com.companyname.android.presentation.core.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel constructor(private val homeRepo: HomeRepo) : BaseViewModel() {

    val sparkTokenRSLiveData: MutableLiveData<String> = MutableLiveData()

    fun getSparkToken() {
        launch {
            postValue(homeRepo.getSparkToken(), sparkTokenRSLiveData)
        }
    }

}