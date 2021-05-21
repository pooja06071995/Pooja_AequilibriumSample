package com.companyname.android

import androidx.lifecycle.MutableLiveData
import com.companyname.android.data.models.Transformer
import com.companyname.android.presentation.utility.AppConstant
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner



@RunWith(MockitoJUnitRunner::class)
class ApplicationTest : BaseTest() {

    private var transformers: MutableList<Transformer> = arrayListOf()
    private var transformerTeamA: List<Transformer> = arrayListOf()
    private var transformerTeamB: List<Transformer> = arrayListOf()

    @Test
    fun testOne() {
        println("Hello")
    }

    @Test
    fun testForTransformerBattle() {
        transformers.add(
            Transformer("T1", "Soundwave", "D", 8, 9, 2, 6, 7, 5, 6, 10, "")
        )

        transformers.add(
            Transformer("T2", "Bluestreak", "A", 6, 6, 7, 9, 5, 2, 9, 7, "")
        )

        transformers.add(
            Transformer("T3", "Hubcap", "A", 4, 4, 4, 4, 4, 4, 4, 4, "")
        )


        transformerTeamA = transformers.filter {
            it.team == AppConstant.TeamA
        }.sortedBy {
            it.rank
        }

        transformerTeamB = transformers.filter {
            it.team == AppConstant.TeamD
        }.sortedBy {
            it.rank
        }

        val winner = viewModel.performBattle(transformerTeamA, transformerTeamB)
        Assert.assertTrue(winner == AppConstant.WinnerD)
    }



}