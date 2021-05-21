package com.companyname.android.presentation.splash



import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.companyname.android.BuildConfig
import com.companyname.android.R
import com.companyname.android.databinding.ActivitySplashBinding
import com.companyname.android.presentation.core.BaseActivity
import com.companyname.android.presentation.utility.*
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun getBaseViewModel() = viewModel

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        Glide.with(this).load(R.mipmap.splash).into(binding.ivSplash)


        Logger.d("Firebase push : " + Prefs.getString(PrefKeys.PushTokenKey, ""))
        Logger.d("Device Android ID : " + getAndroidID())
        Prefs.putString(PrefKeys.AndroidId, getAndroidID())
        if (BuildConfig.DEBUG) {
            binding.tvAppVersion.visible()
        } else {
            binding.tvAppVersion.gone()
        }

        attachObserver()

        checkForToken()
    }

    private fun attachObserver() {
        viewModel.sparkTokenRSLiveData.observe(this, Observer {
            it?.apply {
                hideProgress()
                if (this != null && this.isNotEmpty()) {
                    Prefs.putString(PrefKeys.AuthKey, this)
                    checkForToken()
                } else {
                    toastError(this)
                }
            }
        })
    }

    private fun checkForToken() {
        val token = Prefs.getString(PrefKeys.AuthKey, "")
        if (token.isEmpty()) {
            getSparkToken()
        } else {
            launch {
                delay(AppConstant.TIMEOUT)
                startActivityCustom(IntentHelper.getTransformerListIntent(this@SplashActivity))
                finish()
            }
        }
    }

    private fun getSparkToken() {
        showProgress()
        viewModel.getSparkToken()
    }

}
