package com.personal.accountantAssistant.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.ActivityUtils.startActivity
import com.personal.accountantAssistant.utils.MenuHelper.initializeHomeOptions

class RemittancesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initializeHomeOptions()
        context?.let { startActivity(it, CheckoutActivity::class.java) }
        return inflater.inflate(R.layout.fragment_remittances, container, false)
    }
}