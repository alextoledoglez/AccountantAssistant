package com.personal.accountantAssistant.ui.expenses

import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.expenses.ExpensesListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.showConfirmationFrom
import com.personal.accountantAssistant.extensions.showImportOrExportFrom
import com.personal.accountantAssistant.interfaces.MenuOptionsInterface
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.MenuHelper
import io.reactivex.functions.Action
import org.koin.android.ext.android.inject

abstract class ExpensesFragment<V : BaseViewModel> : BaseFragment<V>(), MenuOptionsInterface {

    abstract override val binding: ViewBinding

    private val activity: Activity? = null
    open var adapter: ExpensesListAdapter? = null
    val databaseManager: DatabaseManager? by inject()

    private var titleImageView: ImageView? = null
    private var subTitleTextView: TextView? = null
    private var recyclerView: RecyclerView? = null

    private var checker: SwitchCompat? = null

    override fun setupView() {
        setHasOptionsMenu(true)
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeAdapter(expensesType: ExpensesType, onChangeAction: Action?) {
        adapter = ExpensesListAdapter(expensesType, context, databaseManager)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                onChangeAction?.let { ActionUtils.runAction(it) }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initializeVisualComponentsFrom(viewRoot: View, type: ExpensesType) {

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
            checker?.isChecked?.let { adapter?.setAllExpensesRecordsActiveFrom(it) }
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
        initializeAdapter(type) { updateHeaderBy(type) }

        if (ExpensesType.isBuy(type)) {
            recyclerView = viewRoot.findViewById(R.id.rvBuys)
        }
        if (ExpensesType.isBill(type)) {
            recyclerView = viewRoot.findViewById(R.id.rvBills)
        }

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        updateHeaderBy(type)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateHeaderBy(type: ExpensesType) {

        when {
            ExpensesType.isBuy(type) -> MenuHelper.initializeBuysOptions()
            ExpensesType.isBill(type) -> MenuHelper.initializeBillsOptions()
            else -> MenuHelper.initializeHomeOptions()
        }

        val isAnyActive = databaseManager?.anyActiveExpensesRecordsBy(type).orFalse()
        val color = context?.getColor(if (isAnyActive) R.color.colorRed else R.color.colorPrimary)

        titleImageView?.setImageResource(R.drawable.ic_money)
        subTitleTextView?.text = adapter?.totalPrice.toString()
        subTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

        color?.let {
            titleImageView?.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN)
            subTitleTextView?.setTextColor(it)
        }

        checker?.isChecked = (databaseManager?.allActiveExpensesRecordsBy(type) == true)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        adapter?.filter?.filter(queryStr)
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
}