package com.personal.accountantAssistant.ui.expenses

import android.app.Activity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.databinding.TitlesBarsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.interfaces.MenuOptionsInterface
import com.personal.accountantAssistant.utils.MenuHelper
import org.koin.android.ext.android.inject

abstract class ExpensesFragment<V : BaseViewModel> : BaseFragment<V>(), MenuOptionsInterface {

    abstract override val binding: ViewBinding
    abstract val adapter: ListAdapter<*, *>
    abstract fun notifyActiveItems(isActive: Boolean)
    abstract fun notifyItemChanged(position: Int, model: ExpenseModel)
    abstract fun notifyItemRemoved(position: Int, model: ExpenseModel)

    private val repository: ExpensesRepository? by inject()
    private val activity: Activity? = null

    override fun initComponents() {
        setHasOptionsMenu(true)
    }

    fun initHeader(header: TitlesBarsBinding) {
        with(header) {
            tvTitle.visibility = View.GONE
            ivMoney.setImageResource(R.drawable.ic_money)
            tvSubtitle.text = String.STR_DEFAULT_MONETARY_VALUE
            tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryStr: String): Boolean {
                    recyclerViewAdapterFilterBy(queryStr)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    recyclerViewAdapterFilterBy(newText)
                    return false
                }
            })
        }
    }

    fun updateHeader(
        header: TitlesBarsBinding, isAnyChecked: Boolean?, isAllChecked: Boolean?, text: String?
    ) {
        val color = context?.getColor(
            if (isAnyChecked.orFalse()) R.color.colorRed else R.color.colorPrimary
        )
        with(header) {
            color?.let {
                ivMoney.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN)
                tvSubtitle.setTextColor(it)
            }
            tvSubtitle.text = text ?: String.STR_DEFAULT_MONETARY_VALUE
            scActive.isChecked = isAllChecked.orFalse()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuHelper.mainMenu = menu
        MenuHelper.enableMenuItemOptions(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.add_record -> addMenuItemClickListener()
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
            R.id.restore_default -> restoreDefaultMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        //adapter.filter?.filter(queryStr)
    }

    private fun importExportMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showImportOrExportFrom(
            R.string.import_export_title,
            this::importMenuItemClickListener,
            this::exportMenuItemClickListener
        )

    private fun deleteAllMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.delete_all_records_title,
            R.string.delete_all_records_message,
            ::deleteAllRecords
        ) {}

    private fun restoreDefaultMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.restore_default_records_title,
            R.string.restore_default_records_message,
            ::restoreDefaultRecords
        ) {}

    fun onExpenseClicked(model: ExpenseModel) {
        ExpenseDetailsFragment.newInstance(model).show(
            requireActivity().supportFragmentManager, String.EMPTY
        )
    }
}