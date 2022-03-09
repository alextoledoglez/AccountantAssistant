package com.personal.accountantAssistant.ui.wallet

import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.CardsListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentWalletBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.interfaces.MenuOptionsInterface
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper
import org.koin.android.ext.android.inject

class WalletFragment : BaseFragment<WalletViewModel>(), MenuOptionsInterface {

    override val binding by viewBinding(FragmentWalletBinding::inflate)
    private val adapter by lazy { CardsListAdapter(::onUpdate, ::onDelete, ::onClick) }
    private val localStorage: LocalStorage? by inject()

    override fun onDestroy() {
        super.onDestroy()
        binding.rvCards.adapter = null
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

    override fun initComponents() {
        setHasOptionsMenu(true)
        MenuHelper.initializeWalletOptions()
        with(binding) {
            srlLoader.setOnRefreshListener { viewModel.loadCards() }
            headerCardTitlesBar.apply {
                titlesBarTitle.visibility = View.GONE
                titleImage.setImageResource(R.drawable.ic_money)
                titlesBarSubtitle.text = String.STR_DEFAULT_MONETARY_VALUE
                titlesBarSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                titleSwitch.setOnClickListener { viewModel.setAllCardsActive(titleSwitch.isChecked) }
                titleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            rvCards.adapter = adapter
        }
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfWallet.displayedChild = it.ordinal }
            isAllChecked.observe(viewLifecycleOwner) {
                binding.headerCardTitlesBar.titleSwitch.isChecked = it.orFalse()
                loadCards()
            }
            isUpdated.observe(viewLifecycleOwner) { loadCards() }
            isDeleted.observe(viewLifecycleOwner) { loadCards() }
            wallet.observe(viewLifecycleOwner) {
                binding.headerCardTitlesBar.titleSwitch.isChecked = it.isAllChecked.orFalse()
                context?.getColor(
                    if (it.isAnyChecked.orFalse()) R.color.colorRed else R.color.colorPrimary
                )?.let { color ->
                    binding.headerCardTitlesBar.apply {
                        titleImage.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
                        titlesBarSubtitle.setTextColor(color)
                    }
                }
                binding.headerCardTitlesBar.titlesBarSubtitle.text = it.total.toCurrencyMaskedStr()
                localStorage?.setAvailableMoney(it.total.orZero().toFloat())
                adapter.submitList(it.cards)
            }
            loadCards()
        }
    }

    override fun addMenuItemClickListener() {
        onClick(CardModel())
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BUY)
    }

    override fun exportMenuItemClickListener() {
        //ImportExportUtils.xlsExport(requireContext(), appDatabase, ExpensesType.BUY)
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllCards()
    }

    override fun restoreDefaultRecords() {
        viewModel.restoreDefaultCards()
    }

    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        adapter.filter.filter(queryStr)
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

    private fun onUpdate(model: CardModel) {
        viewModel.updateCard(model)
    }

    private fun onDelete(model: CardModel) {
        viewModel.deleteCard(model)
    }

    private fun onClick(model: CardModel) {
        WalletDetailsFragment.newInstance(model).show(
            requireActivity().supportFragmentManager, String.EMPTY
        )
    }

    companion object {
        fun newInstance() = WalletFragment()
    }

}