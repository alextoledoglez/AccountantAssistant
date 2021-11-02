package com.personal.accountantAssistant.ui.payments

import android.annotation.SuppressLint
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
import com.personal.accountantAssistant.core.AlertDialogBuilder
import com.personal.accountantAssistant.core.BaseFragment
import com.personal.accountantAssistant.core.BaseViewModel
import com.personal.accountantAssistant.core.extensions.confirmationDialog
import com.personal.accountantAssistant.core.extensions.showImportExportDialog
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.MenuHelper
import io.reactivex.functions.Action
import org.koin.android.ext.android.inject

abstract class PaymentsFragment<V : BaseViewModel> : BaseFragment<V>() {

    abstract override val binding: ViewBinding

    abstract fun addMenuItemClickListener()
    abstract fun importMenuItemClickListener()
    abstract fun exportMenuItemClickListener()
    abstract fun deleteAllPayments()
    abstract fun restoreDefaultPayments()

    private val activity: Activity? = null
    var adapter: PaymentsListAdapter? = null
    val databaseManager: DatabaseManager? by inject()

    private var titleImageView: ImageView? = null
    private var subTitleTextView: TextView? = null
    private var recyclerView: RecyclerView? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
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
            R.id.add_payment -> addMenuItemClickListener()
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
            R.id.restore_default -> restoreDefaultMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeAdapter(paymentsType: PaymentsType, onChangeAction: Action?) {
        adapter = PaymentsListAdapter(paymentsType, context, databaseManager)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                onChangeAction?.let { ActionUtils.runAction(it) }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initializeVisualComponentsFrom(viewRoot: View, type: PaymentsType) {

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
            checker?.isChecked?.let { adapter?.setAllPaymentsRecordsActiveFrom(it) }
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

        //Recycler View
        if (PaymentsType.isBuy(type)) {
            recyclerView = viewRoot.findViewById(R.id.buy_list)
        }
        if (PaymentsType.isBill(type)) {
            recyclerView = viewRoot.findViewById(R.id.bills_list)
        }

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        updateHeaderBy(type)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateHeaderBy(type: PaymentsType) {

        if (PaymentsType.isBuy(type)) {
            MenuHelper.initializeBuysOptions()
        }
        if (PaymentsType.isBill(type)) {
            MenuHelper.initializeBillsOptions()
        }

        val isAnyActive = databaseManager?.anyActivePaymentsRecordsBy(type) ?: false
        val imageRes = if (isAnyActive) R.drawable.ic_red_money else R.drawable.ic_menu_green_money
        val color = context?.getColor(if (isAnyActive) R.color.colorRed else R.color.colorPrimary)

        titleImageView?.setImageResource(imageRes)
        subTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        color?.let { subTitleTextView?.setTextColor(it) }
        subTitleTextView?.text = adapter?.totalPrice.toString()
        checker?.isChecked = (databaseManager?.allActivePaymentsRecordsBy(type) == true)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        val paymentsFilter = adapter?.filter
        paymentsFilter?.filter(queryStr)
    }

    private fun importExportMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).showImportExportDialog(
            R.string.import_export_title,
            this::importMenuItemClickListener,
            this::exportMenuItemClickListener
        )

    private fun deleteAllMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).confirmationDialog(
            R.string.delete_all_records_title,
            R.string.delete_all_records_message,
            ::deleteAllPayments
        )

    private fun restoreDefaultMenuItemClickListener() =
        AlertDialogBuilder(requireContext()).confirmationDialog(
            R.string.restore_default_records_title,
            R.string.restore_default_records_message,
            ::restoreDefaultPayments
        )
}