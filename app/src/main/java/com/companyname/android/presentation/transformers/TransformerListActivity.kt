package com.companyname.android.presentation.transformers

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.companyname.android.R
import com.companyname.android.data.models.Transformer
import com.companyname.android.databinding.ActivityTransformerListBinding
import com.companyname.android.presentation.core.BaseActivity
import com.companyname.android.presentation.transformers.adapters.TransformerListAdapter
import com.companyname.android.presentation.transformers.listener.TransformerItemClickListener
import com.companyname.android.presentation.utility.*
import org.koin.android.viewmodel.ext.android.viewModel

class TransformerListActivity : BaseActivity(), TransformerItemClickListener {

    private val viewModel: TransformerViewModel by viewModel()

    override fun getBaseViewModel() = viewModel

    private lateinit var binding: ActivityTransformerListBinding

    private var adapter: TransformerListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransformerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onResume() {
        super.onResume()
        getTransformerList()
    }

    private fun init() {

        setupToolbar(
            binding.llToolbarMain.toolbar, getString(R.string.app_name), false, Color.BLACK,
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        attachObserver()

        setupAdapter()

        setupClickListener()
    }

    private fun attachObserver() {
        viewModel.transformerListRSLiveData.observe(this, Observer {
            it?.apply {
                hideProgress()
                adapter?.clear()
                adapter?.setData(this.transformers.orEmpty())
                checkForNoDataFound()

                if (binding.swipe.isRefreshing) {
                    binding.swipe.isRefreshing = false
                }
            }
        })

        viewModel.deleteTransformerLiveData.observe(this, Observer {
            it?.apply {
                hideProgress()
                if (this.code() == 204) {
                    toastSuccess(getString(R.string.delete_transformer_success))
                    getTransformerList()
                } else if (this.code() == 401) {
                    toastError(getString(R.string.transformer_not_found))
                } else {
                    toastError(getString(R.string.something_went_wrong))
                }
            }
        })
    }

    private fun setupAdapter() {
        adapter = TransformerListAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun checkForNoDataFound() {
        if (adapter?.itemCount ?: 0 > 0) {
            binding.recyclerView.visible()
            binding.llNoDataFound.tvNoDataFound.invisible()
        } else {
            binding.llNoDataFound.tvNoDataFound.visible()
        }
    }

    private fun getTransformerList() {
        showProgress()
        viewModel.getTransformerList()
    }

    private fun deleteTransformer(transformer: Transformer) {
        showProgress()
        viewModel.deleteTransformer(transformer)
    }

    private fun setupClickListener() {
        binding.btnAddNew.setOnClickListener {
            startActivityCustom(
                IntentHelper.getTransformerIntent(this)
            )
        }

        binding.btnBattle.setOnClickListener {
            checkForBattle()
        }

        binding.swipe.setOnRefreshListener {
            viewModel.getTransformerList()
        }
    }

    private fun checkForBattle() {
        if (adapter?.itemCount ?: 0 > 1) {
            startActivityCustom(
                IntentHelper.getBattleIntent(this)
            )
        } else {
            toastError(getString(R.string.battle_not_possible))
        }
    }


    //#region - listener

    override fun onTransformerItemClick(transformer: Transformer) {
        startActivityCustom(
            IntentHelper.getTransformerIntent(this, transformer)
        )
    }

    override fun onTransformerDeleteClick(transformer: Transformer) {
        showDialog(
            getString(R.string.app_name),
            getString(R.string.delete_transformer_warning),
            getString(R.string.yes), { dialog, which ->
                dialog.dismiss()
                deleteTransformer(transformer)
            },
            getString(R.string.no), { dialog, which ->
                dialog.dismiss()
            }
        )
    }

    //endregion

}