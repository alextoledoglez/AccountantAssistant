package com.personal.accountantAssistant.ui.expenses

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.interfaces.MenuInterface
import com.personal.accountantAssistant.extensions.*

abstract class ExpensesFragment : BaseFragment(), MenuInterface {

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
}