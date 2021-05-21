package com.companyname.android.presentation.utility



import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.companyname.android.data.models.Transformer
import com.companyname.android.presentation.transformers.BattleActivity
import com.companyname.android.presentation.transformers.TransformerActivity
import com.companyname.android.presentation.transformers.TransformerListActivity

class IntentHelper {

    companion object {

        fun getTransformerListIntent(context: Context): Intent {
            return Intent(context, TransformerListActivity::class.java)
        }

        fun getTransformerIntent(context: Context, transformer: Transformer? = null): Intent {
            return Intent(context, TransformerActivity::class.java).also {
                if (transformer != null) {
                    val bundle = Bundle()
                    bundle.putParcelable(Transformer::class.java.simpleName, transformer)
                    it.putExtras(bundle)
                }
            }
        }

        fun getBattleIntent(context: Context): Intent {
            return Intent(context, BattleActivity::class.java)
        }
    }
}