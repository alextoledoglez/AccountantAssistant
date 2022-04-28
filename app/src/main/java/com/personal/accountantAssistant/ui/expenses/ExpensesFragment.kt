package com.personal.accountantAssistant.ui.expenses

import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.bases.interfaces.MenuInterface
import com.personal.accountantAssistant.databinding.LayoutListSummaryBinding
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

abstract class ExpensesFragment<V : BaseViewModel> : BaseFragment<V>(), MenuInterface {

    abstract override val binding: ViewBinding
    abstract val adapter: ListAdapter<*, *>
    abstract fun listAdapterFilterBy(queryStr: String)

    override fun initComponents() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.showMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun importExportMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showImportOrExportFrom(
            R.string.import_export_title,
            this::import,
            this::export
        )

    private fun deleteAllMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.delete_all_records_title,
            R.string.delete_all_records_message,
            ::deleteAll
        ) {}

    fun initLayoutSummary(binding: LayoutListSummaryBinding) {
        with(binding) {
            tvTitle.visibility = View.GONE
            ivMoney.setImageResource(R.drawable.ic_money)
            tvSubtitle.text = String.STR_DEFAULT_MONETARY_VALUE
            tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryStr: String): Boolean {
                    listAdapterFilterBy(queryStr)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    listAdapterFilterBy(newText)
                    return false
                }
            })
        }
    }

    fun updateLayoutSummary(binding: LayoutListSummaryBinding, model: SummaryModel) {
        val isAnyActive = model.isAnyActive()
        val isAllActive = model.isActiveCountEqualTo(adapter.itemCount)
        val totalStr = model.total.toCurrencyMaskedStr()
        with(binding) {
            context?.getCompatColor(isAnyActive, R.color.redColor, R.color.primaryColor)?.let {
                ivMoney.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN)
                tvSubtitle.setTextColor(it)
            }
            tvSubtitle.text = totalStr
            scActive.isChecked = isAllActive
        }
    }
}