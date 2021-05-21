package com.companyname.android.presentation.utility


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.companyname.android.R
import com.companyname.android.data.models.Transformer
import com.companyname.android.presentation.core.BaseActivity
import com.companyname.android.presentation.core.GlideApp
import com.makeramen.roundedimageview.RoundedImageView
import es.dmoral.toasty.Toasty
import gun0912.tedbottompicker.TedBottomPicker
import java.util.*


/**
 * Extension functions for set visibility of any view by calling
 * yourView.visible()
 * yourView.gone()
 */
fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * For start activity
 *
 * @param intent
 * @param requestCode - Nullable
 */
fun Activity.startActivityCustom(intent: Intent, requestCode: Int? = 0) {
    if (requestCode != null) {
        this.startActivityForResult(intent, requestCode)
    } else {
        this.startActivity(intent)
    }
}

fun Fragment.startActivityCustom(intent: Intent, requestCode: Int? = 0) {
    if (requestCode != null) {
        this.startActivityForResult(intent, requestCode)
    } else {
        this.startActivity(intent)
    }
}

/**
 * For load image
 *
 * @param image - image url, file, uri
 */
fun AppCompatImageView.loadImage(image: Any, placeholder: Int? = R.drawable.icn_placeholder_square) {
    GlideApp.with(this.context)
        .load(image)
        .placeholder(placeholder!!)
        .into(this)
}

fun AppCompatImageView.loadImageRound(image: Any, placeholder: Int? = R.drawable.icn_placeholder_square) {
    GlideApp.with(this.context)
        .load(image)
        .placeholder(placeholder!!)
        .circleCrop()
        .into(this)
}

fun RoundedImageView.loadImageRound(image: Any, placeholder: Int? = R.drawable.icn_placeholder_square) {
    GlideApp.with(this.context)
        .load(image)
        .placeholder(placeholder!!)
        .circleCrop()
        .into(this)
}

fun Context.toastSuccess(msg: String) {
    Toasty.success(this, msg).show()
}

fun Context.toastError(msg: String) {
    Toasty.error(this, msg).show()
}

/**
 * For show dialog
 *
 * @param title - title which shown in dialog (application name)
 * @param msg - message which shown in dialog
 * @param positiveText - positive button text
 * @param listener - positive button listener
 * @param negativeText - negative button text
 * @param negativeListener - negative button listener
 * @param icon - drawable icon which shown is dialog
 */
fun Context.showDialog(
    title: String? = this.resources.getString(R.string.app_name),
    msg: String,
    positiveText: String? = this.resources.getString(R.string.ok),
    listener: DialogInterface.OnClickListener? = null,
    negativeText: String? = this.resources.getString(R.string.cancel),
    negativeListener: DialogInterface.OnClickListener? = null,
    icon: Int? = null
) {
    if (BaseActivity.dialogShowing) {
        return
    }
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(msg)
    builder.setCancelable(false)
    builder.setPositiveButton(positiveText) { dialog, which ->
        BaseActivity.dialogShowing = false
        listener?.onClick(dialog, which)
    }
    if (negativeListener != null) {
        builder.setNegativeButton(negativeText) { dialog, which ->
            BaseActivity.dialogShowing = false
            negativeListener.onClick(dialog, which)
        }
    }
    if (icon != null) {
        builder.setIcon(icon)
    }
    builder.create().show()
    BaseActivity.dialogShowing = true
}

/**
 * For validate email, mobile, password
 */
fun Context.isValidEmail(text: String): Boolean {
    return !TextUtils.isEmpty(text)
            && Patterns.EMAIL_ADDRESS.matcher(text).matches()
            && (text.length >= this.resources.getInteger(R.integer.min_length_email))
            && (text.length <= this.resources.getInteger(R.integer.max_length_email))
}

fun Context.isValidPhone(text: String): Boolean {
    return !TextUtils.isEmpty(text)
            && Patterns.PHONE.matcher(text).matches()
            && (text.length >= this.resources.getInteger(R.integer.min_length_mobile))
            && (text.length <= this.resources.getInteger(R.integer.max_length_mobile))
}

fun Context.isValidPassword(text: String): Boolean {
    return !TextUtils.isEmpty(text)
            && (text.length >= this.resources.getInteger(R.integer.min_length_password))
            && (text.length <= this.resources.getInteger(R.integer.max_length_password))
}

fun Context.isPasswordAndConfirmPasswordMatch(password: String, confirmPass: String): Boolean {
    return !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(confirmPass)
            && password.contentEquals(confirmPass)
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
}

fun Context.getDeviceTimeZone(): String {
    val timeZone: String = Calendar.getInstance().timeZone.id
    return timeZone
}

fun Activity.openPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivityForResult(intent, AppConstant.INTENT_SETTINGS)
}

fun Context.getAndroidID(): String {
    return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)
}

fun Context.hideKeyboardFrom(view: View) {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun callFromDialer(mContext: Context, number: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$number")
        mContext.startActivity(callIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        mContext.toastError(mContext.getString(R.string.something_went_wrong))
    }
}

fun getTimeAgo(time1: Long): String {
    val SECOND_MILLIS = 1000
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS

    var time = time1
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = Calendar.getInstance().time.time
    if (time > now || time <= 0) {
        return "in the future"
    }

    val diff = now - time
    return when {
        diff < 48 * HOUR_MILLIS -> "Yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

fun AppCompatTextView.setHtmlString(content: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY))
    } else {
        this.setText(Html.fromHtml(content))
    }
}



fun Transformer.calculateRating() : Int {
    return this.strength + this.intelligence + this.speed + this.endurance + this.firepower
}

fun Transformer.isOptimus() : Boolean {
    return this.name == AppConstant.OPTIMUS
}

fun Transformer.isPredaking() : Boolean {
    return this.name == AppConstant.PREDAKING
}

fun Transformer.isAutobot() : Boolean {
    return this.team == AppConstant.TeamA
}

fun Transformer.isDecepticon() : Boolean {
    return this.team == AppConstant.TeamD
}

