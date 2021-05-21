package com.companyname.android.presentation.transformers

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.companyname.android.R
import com.companyname.android.data.models.Transformer
import com.companyname.android.databinding.ActivityTransformerBinding
import com.companyname.android.presentation.core.BaseActivity
import com.companyname.android.presentation.utility.AppConstant
import com.companyname.android.presentation.utility.toastError
import com.companyname.android.presentation.utility.toastSuccess
import org.koin.android.viewmodel.ext.android.viewModel

class TransformerActivity : BaseActivity() {

    private val viewModel: TransformerViewModel by viewModel()

    override fun getBaseViewModel() = viewModel

    private lateinit var binding: ActivityTransformerBinding

    private var isAdd = true
    private var transformer: Transformer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransformerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    private fun init() {

        var toolbarTitle = getString(R.string.add_transformer)

        if (intent?.extras?.containsKey(Transformer::class.java.simpleName) == true) {
            transformer = intent?.extras?.getParcelable(Transformer::class.java.simpleName)
            isAdd = false
            toolbarTitle = getString(R.string.update_transformer)
            setupData()
        }

        setupToolbar(
            binding.llToolbarMain.toolbar, toolbarTitle, true, Color.BLACK,
            ContextCompat.getColor(this, R.color.colorPrimary), Color.WHITE
        )

        attachObserver()

        setupClickListener()

    }

    private fun attachObserver() {
        viewModel.transformerLiveData.observe(this, Observer {
            it?.apply {
                hideProgress()
                if (this.code() == 200 || this.code() == 201) {
                    if (isAdd) {
                        toastSuccess(getString(R.string.add_transformer_success))
                    } else {
                        toastSuccess(getString(R.string.update_transformer_success))
                    }
                    finish()
                } else {
                    toastError(getString(R.string.something_went_wrong))
                }
            }
        })
    }

    private fun setupData() {
        transformer?.let {
            binding.edtName.setText(it.name.orEmpty())
            if (it.team?.contentEquals(AppConstant.TeamA) == true) {
                binding.rbA.isChecked = true
            } else if (it.team?.contentEquals(AppConstant.TeamD) == true) {
                binding.rbD.isChecked = true
            }
            binding.indicatorStrength.setProgress(it.strength?.toFloat() ?: 1.0f)
            binding.indicatorIntelligence.setProgress(it.intelligence?.toFloat() ?: 1.0f)
            binding.indicatorSpeed.setProgress(it.speed?.toFloat() ?: 1.0f)
            binding.indicatorEndurance.setProgress(it.endurance?.toFloat() ?: 1.0f)
            binding.indicatorRank.setProgress(it.rank?.toFloat() ?: 1.0f)
            binding.indicatorCourage.setProgress(it.courage?.toFloat() ?: 1.0f)
            binding.indicatorFirePower.setProgress(it.firepower?.toFloat() ?: 1.0f)
            binding.indicatorSkill.setProgress(it.skill?.toFloat() ?: 1.0f)
        }
    }

    private fun setupClickListener() {
        binding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val fullName = binding.edtName.text.toString()
        if (fullName.isEmpty()) {
            toastError(getString(R.string.validation_fullname))
            return
        }
        if (!binding.rbA.isChecked && !binding.rbD.isChecked) {
            toastError(getString(R.string.validation_team))
            return
        }

        if (transformer == null) {
            transformer = Transformer(System.currentTimeMillis().toString())
        }

        transformer?.name = fullName
        transformer?.team = if (binding.rbA.isChecked) AppConstant.TeamA else AppConstant.TeamD

        transformer?.strength = binding.indicatorStrength.progress
        transformer?.intelligence = binding.indicatorIntelligence.progress
        transformer?.speed = binding.indicatorSpeed.progress
        transformer?.endurance = binding.indicatorEndurance.progress
        transformer?.rank = binding.indicatorRank.progress
        transformer?.courage = binding.indicatorCourage.progress
        transformer?.firepower = binding.indicatorFirePower.progress
        transformer?.skill = binding.indicatorSkill.progress

        showProgress()
        if (isAdd) {
            viewModel.postTransformer(transformer!!)
        } else {
            viewModel.putTransformer(transformer!!)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}