package com.personal.accountantAssistant.ui.wallet

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.CardsListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentWalletBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.interfaces.MenuOptionsInterface
import org.koin.android.ext.android.inject

class WalletFragment : BaseFragment<WalletViewModel>(), MenuOptionsInterface {

    override val binding by viewBinding(FragmentWalletBinding::inflate)
    private val adapter by lazy {
        CardsListAdapter(::onEditCard, viewModel::switchActiveCard, viewModel::deleteCard)
    }
    private val localStorage: LocalStorage? by inject()

    override fun onDestroy() {
        super.onDestroy()
        binding.rvCards.adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.showMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
            R.id.restore_default -> restoreDefaultMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun initComponents() {
        setHasOptionsMenu(true)
        initLayoutSummary()
        with(binding) {
            srlLoader.setOnRefreshListener { viewModel.loadCards() }
            rvCards.adapter = adapter
        }
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfWallet.displayedChild = it.ordinal }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(it)
                localStorage?.setAvailableMoney(it.total.orZero().toFloat())
                binding.srlLoader.stopRefreshing()
            }
            cards.observe(viewLifecycleOwner) { adapter.submitList(it) { loadSummary() } }
            loadCards()
        }
    }

    override fun importMenuItemClickListener() {
        context?.xlsImport(ExpensesType.BUY)
    }

    override fun exportMenuItemClickListener() {
        //context?.xlsExport(appDatabase, ExpensesType.BUY)
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllCards()
    }

    override fun restoreDefaultRecords() {
        viewModel.restoreDefaultCards()
    }

    private fun initLayoutSummary() {
        with(binding.lytSummary) {
            tvTitle.visibility = View.GONE
            ivMoney.setImageResource(R.drawable.ic_money)
            tvSubtitle.text = String.STR_DEFAULT_MONETARY_VALUE
            tvSubtitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24f)
            scActive.setOnClickListener { viewModel.setAllCardsActive(scActive.isChecked) }
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

    private fun updateLayoutSummary(model: SummaryModel) {
        val isAnyActive = model.isAnyActive()
        val totalStr = model.total.toCurrencyMaskedStr()
        val isAllActive = model.isActiveCountEqualTo(adapter.itemCount)
        with(binding.lytSummary) {
            context?.getCompatColor(isAnyActive, R.color.colorRed, R.color.colorPrimary)?.let {
                ivMoney.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN)
                tvSubtitle.setTextColor(it)
            }
            tvSubtitle.text = totalStr
            scActive.isChecked = isAllActive
        }
    }

    private fun listAdapterFilterBy(queryStr: String) {
        if (queryStr.isNotBlank())
            adapter.filter.filter(queryStr)
        else
            viewModel.loadCards()
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

    fun onEditCard(model: CardModel) {
        WalletDetailsFragment.showDialogFragment(
            model, viewModel::saveCard, requireActivity().supportFragmentManager
        )
    }

    companion object {
        fun newInstance() = WalletFragment()
    }

}