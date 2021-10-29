package com.personal.accountantAssistant.ui.buys

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.ui.payments.PaymentsDetailsFragment
import com.personal.accountantAssistant.ui.payments.PaymentsFragment
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.toBuys
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.ImportExportUtils

class BuysFragment : PaymentsFragment<BuysViewModel>() {

    override val binding: ViewBinding by viewBinding(FragmentBuysBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        super.setupView()
        initializeVisualComponentsFrom(binding.root, PaymentsType.BUY)
    }

    override fun addMenuItemClickListener() {
        PaymentsDetailsFragment.newInstance(Payments().toBuys()).apply {
            onSaveActionListener = { adapter?.notifyDataSetChanged() }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, PaymentsType.BUY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun exportMenuItemClickListener() {
        ImportExportUtils.xlsExport(requireContext(), PaymentsType.BUY)
    }

    override fun deleteAllPayments() {
        deleteAllBuysRecords()?.let {
            ActionUtils.conditionalActions(it, { /*refreshRecyclerView()*/ })
        }
    }

    override fun restoreDefaultPayments() {
        deleteAllBuysRecords()?.let {
            databaseManager?.insertDefaultBuysRecords()
            //refreshRecyclerView()
        }
    }

    private fun deleteAllBuysRecords(): Boolean? {
        return databaseManager?.deleteAllBuysRecord()
            ?.let { databaseManager?.isNotDefaultRecord(it) }
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}