package com.companyname.android.data.models

import android.os.Parcelable
import com.companyname.android.presentation.utility.Logger
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TransformerListRS(
    @SerializedName("transformers") var transformers: List<Transformer>? = arrayListOf()
) : BaseResponse()

@Parcelize
data class Transformer(
    @SerializedName("id") var id: String? = "",
    @SerializedName("name") var name: String? = "",
    @SerializedName("team") var team: String? = "",
    @SerializedName("strength") var strength: Int = 0,
    @SerializedName("intelligence") var intelligence: Int = 0,
    @SerializedName("speed") var speed: Int = 0,
    @SerializedName("endurance") var endurance: Int = 0,
    @SerializedName("rank") var rank: Int = 0,
    @SerializedName("courage") var courage: Int = 0,
    @SerializedName("firepower") var firepower: Int = 0,
    @SerializedName("skill") var skill: Int = 0,
    @SerializedName("team_icon") var teamIcon: String? = ""
) : Parcelable, BaseResponse() {

    fun getRating(): Float {
        var rating = 0.0f
        val total = (strength + intelligence + speed + endurance + firepower).toFloat()
        rating = (total / 10)
        Logger.d("total : $total ,,, Rating : $rating")
        return rating
    }

}
