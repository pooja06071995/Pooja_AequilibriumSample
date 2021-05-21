package com.companyname.android.presentation.transformers

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.companyname.android.R
import com.companyname.android.data.models.Transformer
import com.companyname.android.databinding.ActivityBattleBinding
import com.companyname.android.presentation.core.BaseActivity
import com.companyname.android.presentation.splash.SplashActivity
import com.companyname.android.presentation.utility.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.min

class BattleActivity : BaseActivity() {

    private val viewModel: TransformerViewModel by viewModel()

    override fun getBaseViewModel() = viewModel

    private lateinit var binding: ActivityBattleBinding

    private var transformers: List<Transformer> = arrayListOf()
    private var transformerTeamA: List<Transformer> = arrayListOf()
    private var transformerTeamB: List<Transformer> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        setupToolbar(
            binding.llToolbarMain.toolbar, getString(R.string.battle), true, Color.BLACK,
            ContextCompat.getColor(this, R.color.colorPrimary), Color.WHITE
        )

        attachObserver()

        getTransformerList()

        setupClickListener()
    }

    private fun attachObserver() {
        viewModel.transformerListRSLiveData.observe(this, Observer {
            it?.apply {
                hideProgress()
                this@BattleActivity.transformers = this.transformers.orEmpty()
                setupTeams()
            }
        })

        viewModel.winnerLiveData.observe(this, Observer {
            showWinner(it)
        })
    }

    private fun getTransformerList() {
        showProgress()
        viewModel.getTransformerList()
    }

    private fun setupClickListener() {
        binding.btnBattle.setOnClickListener {
            performBattle()
        }
    }

    private fun setupTeams() {

        transformerTeamA = transformers.filter {
            it.team == AppConstant.TeamA
        }.sortedBy {
            it.rank
        }
        if (transformerTeamA.isNotEmpty() == true) {
            binding.tvTeamA.text = transformerTeamA.joinToString {
                it.name.orEmpty() + " (" + it.calculateRating() + ")"
            }
            binding.ivTeamA.loadImage(
                transformerTeamA.get(0).teamIcon.orEmpty(), R.drawable.icn_placeholder_square
            )
        }


        transformerTeamB = transformers.filter {
            it.team == AppConstant.TeamD
        }.sortedBy {
            it.rank
        }
        if (transformerTeamB.isNotEmpty() == true) {
            binding.tvTeamB.text = transformerTeamB.joinToString {
                it.name.orEmpty() + " (" + it.calculateRating() + ")"
            }
            binding.ivTeamB.loadImage(
                transformerTeamB.get(0).teamIcon.orEmpty(), R.drawable.icn_placeholder_square
            )
        }

        if (transformerTeamA.isEmpty() || transformerTeamB.isEmpty()) {
            toastError(getString(R.string.battle_not_possible))
            finish()
        }
    }

    private fun performBattle() {
        if (transformerTeamA.isNotEmpty() == true && transformerTeamB.isNotEmpty() == true) {
            viewModel.performBattle(transformerTeamA, transformerTeamB)
        } else {
            toastError(getString(R.string.battle_warning))
        }
    }

    private fun showWinner(winner: String) {
        var title = ""
        when (winner) {
            AppConstant.WinnerA -> title = getString(R.string.team_A_win)
            AppConstant.WinnerD -> title = getString(R.string.team_D_win)
            AppConstant.WinnerTie -> title = getString(R.string.tie)
            AppConstant.WinnerDistroy -> title = getString(R.string.distroy_message)
        }

        showDialog(getString(R.string.app_name), title,
            getString(R.string.ok), { dialog, which ->
                val intent = Intent(this,SplashActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            })
    }


}