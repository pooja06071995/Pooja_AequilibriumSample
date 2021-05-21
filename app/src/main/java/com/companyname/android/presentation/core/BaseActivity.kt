package com.companyname.android.presentation.core



import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.companyname.android.R
import com.companyname.android.domain.exceptions.MyException
import com.companyname.android.presentation.utility.Logger
import com.companyname.android.presentation.utility.showDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        var dialogShowing = false
    }

    lateinit var toolbar1: Toolbar
    private var needToShowBackButton: Boolean? = false

    lateinit var job: Job
    private var progress: CustomProgressDialog? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    abstract fun getBaseViewModel(): BaseViewModel?

    private val APP_UPDATE_REQUEST_CODE = 1991

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        progress = CustomProgressDialog(this)

        attachBaseObserver()

    }

    private fun attachBaseObserver() {
        getBaseViewModel()?.exceptionLiveData?.observe(this, Observer {
            hideProgress()
            it?.apply {
                when (this) {
                    is MyException.InternetConnectionException -> {
                        showDialog(
                            getString(R.string.app_name),
                            getString(R.string.exception_error_network),
                            getString(R.string.ok), { dialog, which ->
                                dialog.dismiss()
                                finish()
                            }
                        )
                    }
                    is MyException.JsonException -> showErrorDialog(getString(R.string.exception_error_unparceble))
                    is MyException.ServerNotAvailableException -> showErrorDialog(getString(R.string.exception_error_server))
                    is MyException.DatabaseException -> showErrorDialog(getString(R.string.exception_error_database))
                    is MyException.NetworkCallCancelException -> {
                    }
                    else -> {
                        var message = it.message
                        if (message?.isEmpty() == true) {
                            message = it.localizedMessage
                        }
                        showErrorDialog("Unknown error : " + message)
                    }
                }
            }
        })
    }

    private fun showErrorDialog(message: String) {
        showDialog(
            getString(R.string.app_name),
            message,
            getString(R.string.ok), DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    protected fun addFragment(
        @IdRes containerViewId: Int, fragment: Fragment,
        fragmentTag: String
    ) {
        supportFragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, fragmentTag)
            .disallowAddToBackStack()
            .commit()
    }

    protected fun replaceFragment(
        @IdRes containerViewId: Int, fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean? = false
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment, fragmentTag)
            .addToBackStack(if (addToBackStack!!) fragment::class.java.simpleName else null)
            .commit()
    }

    protected fun replaceFragmentWithPop(
        @IdRes containerViewId: Int, fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean? = false
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment, fragmentTag)
            .disallowAddToBackStack()
            .commit()
    }


    fun showProgress() {
        if (!this.isFinishing) {
            progress?.show()
        }
    }

    fun hideProgress() {
        if (!this.isFinishing && progress?.isShowing == true) {
            progress?.dismiss()
        }
    }

    /**
     * Method for perform multiple action by single line
     *
     * @param toolbar - pass your toolbar added in your layout
     * @param title - screen title
     * @param needToShowBackButton - want to show back button or not in activity
     * @param titleTextColor - color for toolbar title
     * @param toolbarColor - color for toolbar background
     * @param backButtonColor - color for back button
     */
    fun setupToolbar(
        toolbar: Toolbar,
        title: String,
        needToShowBackButton: Boolean? = false,
        titleTextColor: Int? = R.color.colorBlack,
        toolbarColor: Int? = null,
        backButtonColor: Int? = null
    ) {
        this.toolbar1 = toolbar
        this.needToShowBackButton = needToShowBackButton
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (needToShowBackButton!!) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setHomeButtonEnabled(true)
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black)
        }
        toolbar.findViewById<AppCompatTextView>(R.id.txt_header).text = title
        toolbar.findViewById<AppCompatTextView>(R.id.txt_header).setTextColor(titleTextColor!!)

        if (toolbarColor != null) {
            toolbar.background = ColorDrawable(toolbarColor)
        } else {
            toolbar.background = ColorDrawable(Color.TRANSPARENT)
        }

        if (backButtonColor == Color.WHITE) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.invisible, menu)
        return super.onCreateOptionsMenu(menu)
    }


    //#region - app update

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    //private lateinit var appUpdateManager: FakeAppUpdateManager
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                    else -> Logger.d("InstallStateUpdatedListener: state: " + installState.installStatus())
                }
            }
        }
    }

    fun checkForAppUpdate() {

        //appUpdateManager = FakeAppUpdateManager(this)
        //appUpdateManager.setUpdatePriority(AppUpdateType.IMMEDIATE)
        //appUpdateManager.setUpdateAvailable(2)
        appUpdateManager.registerListener(appUpdatedListener)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        Logger.d("appUpdateInfo called")
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Logger.d("appUpdateInfo : " + appUpdateInfo.updateAvailability())
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }
                    //if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(appUpdatedListener)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        installType!!,
                        this,
                        APP_UPDATE_REQUEST_CODE
                    )
                    Logger.d("appUpdateInfo : " + "installType : " + installType)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }

        appUpdateInfoTask.addOnFailureListener {
            Logger.d("appUpdateInfo : " + it.localizedMessage.toString())
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            findViewById(R.id.swipe),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }


    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                Logger.d("appUpdateInfo onResume : " + appUpdateInfo.updateAvailability())
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
                //Check if Immediate update is required
                try {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            APP_UPDATE_REQUEST_CODE
                        )
                    }
                    Logger.d("appUpdateInfo onResume : " + "installType : " + AppUpdateType.IMMEDIATE)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
    }

    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "App Update failed, please try again on the next app launch.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}