package com.personal.accountantAssistant.ui.wallet

import android.os.Build
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.wallet.CardsListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.databinding.FragmentWalletBinding
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.interfaces.MenuOptionsInterface
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper
import org.koin.android.ext.android.inject

class WalletFragment : BaseFragment<WalletViewModel>(), MenuOptionsInterface {

    override val binding: ViewBinding by viewBinding(FragmentWalletBinding::inflate)
    private val databaseManager: DatabaseManager? by inject()
    private val localStorage: LocalStorage? by inject()

    private var titleImageView: ImageView? = null
    private var subTitleTextView: TextView? = null
    private var cardsAdapter: CardsListAdapter? = null
    private var cardsRecyclerView: RecyclerView? = null

    private var checker: SwitchCompat? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        setHasOptionsMenu(true)
        initializeVisualComponentsFrom(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuHelper.mainMenu = menu
        MenuHelper.enableMenuItemOptions(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.add_record -> addMenuItemClickListener()
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
            R.id.restore_default -> restoreDefaultMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeAdapter() {
        cardsAdapter = CardsListAdapter(context, databaseManager)
        cardsAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateHeaderBy()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initializeVisualComponentsFrom(viewRoot: View) {

        val headerCardTitlesBar = viewRoot.findViewById<View>(R.id.header_card_titles_bar)

        //Title
        val headerCardTitle = headerCardTitlesBar.findViewById<TextView>(R.id.titles_bar_title)
        headerCardTitle.visibility = View.GONE

        //Image
        titleImageView = headerCardTitlesBar.findViewById(R.id.title_image)

        //Subtitle
        subTitleTextView = headerCardTitlesBar.findViewById(R.id.titles_bar_subtitle)
        subTitleTextView?.text = java.lang.String.valueOf(Constants.DEFAULT_VALUE)

        //Switch
        checker = headerCardTitlesBar.findViewById(R.id.title_switch)
        checker?.setOnClickListener {
            checker?.isChecked?.let { cardsAdapter?.setAllCardsRecordsActiveFrom(it) }
        }

        //Search View
        val searchView = headerCardTitlesBar.findViewById<SearchView>(R.id.title_search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryStr: String): Boolean {
                recyclerViewAdapterFilterBy(queryStr)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recyclerViewAdapterFilterBy(newText)
                return false
            }
        })
        initializeAdapter()
        cardsRecyclerView = viewRoot.findViewById(R.id.rvCards)
        cardsRecyclerView?.adapter = cardsAdapter
        updateHeaderBy()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateHeaderBy() {

        MenuHelper.initializeWalletOptions()

        val isAnyActive = databaseManager?.isAnyCardRecordActive() ?: false
        val imageRes = if (isAnyActive) R.drawable.ic_red_money else R.drawable.ic_menu_green_money
        val color = context?.getColor(if (isAnyActive) R.color.colorRed else R.color.colorPrimary)

        titleImageView?.setImageResource(imageRes)

        subTitleTextView?.text = cardsAdapter?.totalValue.toString()
        subTitleTextView?.setTextColor(color ?: R.color.colorBlack)
        subTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        localStorage?.setAvailableMoney(cardsAdapter?.totalValue.orZero().toFloat())

        checker?.isChecked = (databaseManager?.isAllCardRecordsActive() == true)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        cardsAdapter?.filter?.filter(queryStr)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun addMenuItemClickListener() {
        WalletDetailsFragment.newInstance(CardEntity()).apply {
            onSaveActionListener = { cardsAdapter?.notifyCardsAddedOrChanged(it) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BUY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun exportMenuItemClickListener() {
        ImportExportUtils.xlsExport(requireContext(), ExpensesType.BUY)
    }

    override fun deleteAllRecords() {
        deleteAllCardsRecords()?.let {
            ActionUtils.conditionalActions(it, { /*refreshRecyclerView()*/ })
        }
    }

    override fun restoreDefaultRecords() {
        deleteAllCardsRecords()?.let {
            databaseManager?.insertDefaultCardsRecords()
            //refreshRecyclerView()
        }
    }

    private fun deleteAllCardsRecords(): Boolean? {
        return databaseManager?.deleteAllCardsRecords()
            ?.let { databaseManager?.isNotDefaultRecord(it) }
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
        )

    private fun restoreDefaultMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.restore_default_records_title,
            R.string.restore_default_records_message,
            ::restoreDefaultRecords
        )

    companion object {
        fun newInstance() = WalletFragment()
    }

}