package com.personal.accountantAssistant.ui.wallet

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.interfaces.MenuInterface
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentWalletBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

class WalletFragment : BaseFragment<WalletViewModel>(), MenuInterface {

    override val binding by viewBinding(FragmentWalletBinding::inflate)

    private val lytSummary by lazy { binding.lytSummary }
    private val lytContent by lazy { binding.lytContent }
    private val srlContent by lazy { lytContent.srlContent }
    private val vfContent by lazy { lytContent.vfContent }
    private val rvContent by lazy { lytContent.rvContent }

    private val adapter by lazy {
        CardsListAdapter(::onEditCard, viewModel::switchActiveCard, ::onDeleteCard)
    }

    override fun onDestroy() {
        super.onDestroy()
        rvContent.destroyAdapter()
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

    override fun initComponents() {
        setHasOptionsMenu(true)
        initLayoutSummary()
        srlContent.setOnRefreshListener { viewModel.loadCards() }
        rvContent.setGridLayoutAdapter(adapter)
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { srlContent.updateRefreshing(it.orFalse()) }
            flipper.observe(viewLifecycleOwner) { vfContent.updateDisplayedChild(it.ordinal) }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(it)
                srlContent.stopRefreshing()
            }
            cards.observe(viewLifecycleOwner) {
                adapter.submitList(it) {
                    loadSummary()
                    srlContent.stopRefreshing()
                    rvContent.scrollToTop()
                }
            }
            loadCards()
        }
    }

    override fun import() {
        context?.xlsImport(ExpensesType.BUY)
    }

    override fun export() {
        //context?.xlsExport(appDatabase, ExpensesType.BUY)
    }

    override fun deleteAll() {
        viewModel.deleteAllCards()
    }

    private fun initLayoutSummary() {
        with(lytSummary) {
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
        with(lytSummary) {
            context?.getCompatColor(isAnyActive, R.color.redColor, R.color.primaryColor)?.let {
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