package com.companyname.android.presentation.utility



import com.pixplicity.easyprefs.library.Prefs

class PrefKeys {
    companion object {

        const val AuthKey = "AuthKey"
        const val PushTokenKey = "push_token_key"

        const val Latitude = "latitude"
        const val Longitude = "longitude"
        const val AndroidId = AppConstant.vDeviceUniqueId

        fun isUserLoggedIn(): Boolean {
            return getAuthKey().isNotEmpty()
        }

        fun getAuthKey(): String {
            return Prefs.getString(AuthKey, "")
        }

        fun getFirebasePushToken(): String {
            return Prefs.getString(PushTokenKey, "")
        }

        fun getLatitude(): String {
            return Prefs.getString(Latitude, "")
        }

        fun getLongitude(): String {
            return Prefs.getString(Longitude, "")
        }

        fun getLanguageSelectedCapital(): String {
            val language = Prefs.getString(LocaleHelper.SELECTED_LANGUAGE, "en")
            //return language.toUpperCase()
            return language
        }

        fun getAndroidId(): String {
            return Prefs.getString(AndroidId, "")
        }
    }
}