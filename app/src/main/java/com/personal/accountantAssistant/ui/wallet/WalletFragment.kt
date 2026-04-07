package com.personal.accountantAssistant.ui.wallet

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.compose.runtime.Composable
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.interfaces.MenuInterface
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletFragment : BaseFragment(), MenuInterface {

    private val viewModel: WalletViewModel by viewModel()

    override fun initComponents() {
        setHasOptionsMenu(true)
    }

    override fun initObservers() {
        viewModel.loadCards()
    }

    @Composable
    override fun ScreenContent() {
        WalletScreen(
            viewModel = viewModel,
            onEdit = ::onEditCard,
            onActive = viewModel::switchActiveCard,
            onDelete = ::onDeleteCard
        )
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

    override fun import() {
        context?.xlsImport(ExpensesType.BUY)
    }

    override fun export() {}

    override fun deleteAll() {
        viewModel.deleteAllCards()
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

    private fun onDeleteCard(model: CardModel) {
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.delete_record_title,
            R.string.delete_record_message,
            { viewModel.deleteCard(model) }
        ) {}
    }

    fun onEditCard(model: CardModel) {
        WalletDetailsFragment.showDialogFragment(
            model, viewModel::saveCard, requireActivity().supportFragmentManager
        )
    }

    companion object {
        fun newInstance() = WalletFragment()
    }
}