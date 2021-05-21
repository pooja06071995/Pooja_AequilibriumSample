package com.companyname.android.presentation.transformers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.companyname.android.R
import com.companyname.android.data.models.Transformer
import com.companyname.android.databinding.CellTransformerItemBinding
import com.companyname.android.presentation.core.BaseRecyclerViewAdapter
import com.companyname.android.presentation.transformers.listener.TransformerItemClickListener
import com.companyname.android.presentation.utility.AppConstant
import com.companyname.android.presentation.utility.loadImageRound

class TransformerListAdapter(val listener: TransformerItemClickListener) :
    BaseRecyclerViewAdapter<Transformer>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding =
            CellTransformerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun bindData(holder: RecyclerView.ViewHolder?, item: Transformer?, position: Int) {
        val viewHolder = holder as ViewHolder
        if (item != null) {
            viewHolder.bindData(item)
        }
    }

    inner class ViewHolder(private val itemBinding: CellTransformerItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                listener.onTransformerItemClick(getItem(adapterPosition))
            }

            itemBinding.ivEdit.setOnClickListener {
                listener.onTransformerItemClick(getItem(adapterPosition))
            }

            itemBinding.ivDelete.setOnClickListener {
                listener.onTransformerDeleteClick(getItem(adapterPosition))
            }
        }

        fun bindData(item: Transformer) {
            //set or bind data
            itemBinding.viewmodel = item
            itemBinding.ivImage.loadImageRound(
                item.teamIcon.orEmpty(),
                R.drawable.ic_user_placeholder
            )
            itemBinding.ratingBar.rating = item.getRating()
            itemBinding.tvTitle.text = item.name
            if (item.team?.contentEquals(AppConstant.TeamA) == true) {
                itemBinding.tvSubTitle.text =
                    itemBinding.tvSubTitle.context.getString(R.string.team_autobot)
            } else {
                itemBinding.tvSubTitle.text =
                    itemBinding.tvSubTitle.context.getString(R.string.team_decepticon)

            }
        }
    }
}