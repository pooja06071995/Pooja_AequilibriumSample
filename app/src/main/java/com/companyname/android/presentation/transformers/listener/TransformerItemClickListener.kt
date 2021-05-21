package com.companyname.android.presentation.transformers.listener

import com.companyname.android.data.models.Person
import com.companyname.android.data.models.Transformer




interface TransformerItemClickListener {
    fun onTransformerItemClick(transformer: Transformer)
    fun onTransformerDeleteClick(transformer: Transformer)
}