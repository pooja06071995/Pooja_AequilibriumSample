package com.companyname.android.data.models



import com.companyname.android.presentation.utility.AppConstant
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


open class BasePRQ(
    @SerializedName(AppConstant.authKey) var vAuthKey: String? = "",
    @SerializedName(AppConstant.page) var page: String? = "",
    @SerializedName(AppConstant.search_term) var search_term: String? = "",
    @SerializedName(AppConstant.language) var language: String? = ""
)

open class BaseResponse(
    @Expose
    @SerializedName("status")
    var status: Int? = 0,
    @Expose
    @SerializedName("message")
    var message: String? = ""
) {
    fun isSuccess() : Boolean{
        return status == 1
    }
}