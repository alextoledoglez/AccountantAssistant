package com.personal.accountantAssistant.ui.bills

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.ui.payments.PaymentsDetailsFragment
import com.personal.accountantAssistant.ui.payments.PaymentsFragment
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.toBills
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.ImportExportUtils

class BillsFragment : PaymentsFragment<BillsViewModel>() {

    override val binding: ViewBinding by viewBinding(FragmentBillsBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        super.setupView()
        initializeVisualComponentsFrom(binding.root, PaymentsType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun addMenuItemClickListener() {
        PaymentsDetailsFragment.newInstance(Payments().toBills()).apply {
            onSaveActionListener = { adapter?.notifyItemAddedOrChanged(it) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, PaymentsType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun exportMenuItemClickListener() {
        ImportExportUtils.xlsExport(requireContext(), PaymentsType.BILL)
    }

    override fun deleteAllPayments() {
        deleteAllBillsRecords()?.let {
            ActionUtils.conditionalActions(it, { /*refreshRecyclerView()*/ })
        }
    }

    override fun restoreDefaultPayments() {
        deleteAllBillsRecords()?.let {
            databaseManager?.insertDefaultBillsRecords()
            //refreshRecyclerView()
        }
    }

    private fun deleteAllBillsRecords(): Boolean? {
        return databaseManager?.deleteAllBillsRecord()
            ?.let { databaseManager?.isNotDefaultRecord(it) }
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}