package com.companyname.android.presentation.transformers

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.companyname.android.data.contract.HomeRepo
import com.companyname.android.data.models.Transformer
import com.companyname.android.data.models.TransformerListRS
import com.companyname.android.di.viewModelModule
import com.companyname.android.presentation.core.BaseViewModel
import com.companyname.android.presentation.splash.SplashActivity
import com.companyname.android.presentation.utility.*
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.min

class TransformerViewModel(private val homeRepo: HomeRepo) : BaseViewModel() {

    val deleteTransformerLiveData: MutableLiveData<Response<TransformerListRS>> = MutableLiveData()
    val transformerListRSLiveData: MutableLiveData<TransformerListRS> = MutableLiveData()
    val transformerLiveData: MutableLiveData<Response<Transformer>> = MutableLiveData()

    val winnerLiveData: MutableLiveData<String> = MutableLiveData()

    fun getTransformerList() {
        launch {
            postValue(homeRepo.getTransformerList(), transformerListRSLiveData)
        }
    }

    fun postTransformer(transformer: Transformer) {
        launch {
            postValue(homeRepo.postTransformer(transformer), transformerLiveData)
        }
    }

    fun putTransformer(transformer: Transformer) {
        launch {
            postValue(homeRepo.putTransformer(transformer), transformerLiveData)
        }
    }

    fun deleteTransformer(transformer: Transformer) {
        launch {
            postValue(
                homeRepo.deleteTransformer(transformer.id.toString()),
                deleteTransformerLiveData
            )
        }
    }


    fun performBattle(teamA: List<Transformer>, teamB: List<Transformer>): String {
        var winner: String = ""
        val minimumMatches = min(teamA.size, teamB.size)
        val matchResultList: MutableList<String> = arrayListOf()
        for (i in 0..(minimumMatches - 1)) {
            val first = teamA.get(i)
            val second = teamB.get(i)
            val result = battle(first, second)
            matchResultList.add(result)
        }

        val winCountA = matchResultList.filter {
            AppConstant.TeamA == it
        }.orEmpty()
        val winCountD = matchResultList.filter {
            AppConstant.TeamD == it
        }.orEmpty()

        if (winCountA.size > winCountD.size) {
            winner = AppConstant.WinnerA
            winnerLiveData.postValue(AppConstant.WinnerA)
        } else if (winCountA.size < winCountD.size) {
            winner = AppConstant.WinnerD
            winnerLiveData.postValue(AppConstant.WinnerD)
        } else {

            teamA.forEachIndexed { index, item ->
                if (item.name.equals(AppConstant.PREDAKING)) {
                    teamB.forEachIndexed { index, item ->
                        if (item.name.equals(AppConstant.PREDAKING)||item.name.equals(AppConstant.OPTIMUS)) {

                            winner = AppConstant.WinnerDistroy
                            winnerLiveData.postValue(AppConstant.WinnerDistroy)
                            launch {
                                postValue(
                                    homeRepo.deleteTransformer(item.id.toString()),
                                    deleteTransformerLiveData


                                )
                                teamA.forEachIndexed { index, item ->

                                    launch {
                                        postValue(
                                            homeRepo.deleteTransformer(item.id.toString()),
                                            deleteTransformerLiveData



                                        )
                                    }

                                }
                            }


                        }

                    }
                } else if (item.name.equals(AppConstant.OPTIMUS)) {
                    teamB.forEachIndexed { index, item ->
                        if (item.name.equals(AppConstant.OPTIMUS)||item.name.equals(AppConstant.PREDAKING)) {

                            winner = AppConstant.WinnerDistroy
                            winnerLiveData.postValue(AppConstant.WinnerDistroy)
                            launch {
                                postValue(

                                    homeRepo.deleteTransformer(item.id.toString()),
                                    deleteTransformerLiveData



                                )
                                teamA.forEachIndexed { index, item ->

                                    launch {
                                        postValue(
                                            homeRepo.deleteTransformer(item.id.toString()),
                                            deleteTransformerLiveData



                                        )

                                    }

                                }
                            }


                        }
                    }

                } else {
                    winner = AppConstant.WinnerTie
                    winnerLiveData.postValue(AppConstant.WinnerTie)
                }

            }


        }

        return winner
    }


    private fun battle(first: Transformer, second: Transformer): String {
        val specialRuleWinner = checkForSpecialRule(first, second)
        if (specialRuleWinner != AppConstant.WinnerTie) {
            return specialRuleWinner
        }

        val match1Winner = checkForMatch1(first, second)
        if (match1Winner != AppConstant.WinnerTie) {
            return match1Winner
        }

        val match2Winner = checkForMatch2(first, second)
        if (match2Winner != AppConstant.WinnerTie) {
            return match2Winner
        }

        return checkForMatch3(first, second)
    }


    private fun checkForSpecialRule(first: Transformer, second: Transformer): String {
        return when {
            first.isOptimus() && second.isPredaking() -> AppConstant.WinnerDistroy
            first.isPredaking() && second.isOptimus() -> AppConstant.WinnerDistroy
            first.isPredaking() && second.isPredaking() -> AppConstant.WinnerDistroy
            first.isOptimus() && second.isOptimus() -> AppConstant.WinnerDistroy
            first.isOptimus() || first.isPredaking() -> AppConstant.WinnerA
            second.isPredaking() || second.isOptimus() -> AppConstant.WinnerD
            first.isOptimus()  -> AppConstant.WinnerA
            second.isPredaking()  -> AppConstant.WinnerD
            else -> AppConstant.WinnerTie


        }
    }

    private fun checkForMatch1(first: Transformer, second: Transformer): String {
        return when {
            (first.courage - second.courage < -4) && (first.strength - second.strength < -3) && first.isAutobot() -> AppConstant.WinnerD
            (second.courage - first.courage < -4) && (second.strength - first.strength < -3) && second.isDecepticon() -> AppConstant.WinnerA
            else -> AppConstant.WinnerTie
        }
    }

    private fun checkForMatch2(first: Transformer, second: Transformer): String {
        return when {
            (first.skill - second.skill >= 3) && first.isAutobot() -> AppConstant.WinnerA
            (second.skill - first.skill >= 3) && second.isDecepticon() -> AppConstant.WinnerD
            else -> AppConstant.WinnerTie
        }
    }

    private fun checkForMatch3(first: Transformer, second: Transformer): String {
        return when {
            (first.calculateRating() > second.calculateRating()) && first.isAutobot() -> AppConstant.WinnerA
            (first.calculateRating() < second.calculateRating()) && second.isDecepticon() -> AppConstant.WinnerD
            else -> AppConstant.WinnerTie
        }
    }


}