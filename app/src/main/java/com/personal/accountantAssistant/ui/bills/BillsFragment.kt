package com.personal.accountantAssistant.ui.bills

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.PaymentsFragmentsUtils

class BillsFragment : Fragment() {
    private var paymentsFragmentsUtils: PaymentsFragmentsUtils? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_bills, container, false)
        paymentsFragmentsUtils = PaymentsFragmentsUtils(context, requireActivity(), PaymentsType.BILL)
        paymentsFragmentsUtils?.initializeVisualComponentsFrom(viewRoot)
        return viewRoot
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        paymentsFragmentsUtils?.onDetailsActivityResult(requestCode, resultCode, resultData)
    }
}