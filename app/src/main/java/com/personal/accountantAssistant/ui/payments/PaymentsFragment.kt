package com.personal.accountantAssistant.ui.payments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.BaseFragment
import com.personal.accountantAssistant.core.BaseViewModel
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.*
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
    val databaseManager: DatabaseManager? by inject()

    private var titleImageView: ImageView? = null
    private var subTitleTextView: TextView? = null
    private var recyclerView: RecyclerView? = null
    var adapter: PaymentsListAdapter? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var checker: androidx.appcompat.widget.SwitchCompat? = null

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
    fun initializeVisualComponentsFrom(viewRoot: View, paymentsType: PaymentsType) {

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
        initializeAdapter(paymentsType) { updateRecyclerView(paymentsType) }

        //Recycler View
        if (PaymentsType.isBuy(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.buy_list)
        }
        if (PaymentsType.isBill(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.bills_list)
        }
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        updateRecyclerView(paymentsType)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateRecyclerView(paymentsType: PaymentsType) {
        if (PaymentsType.isBuy(paymentsType)) {
            MenuHelper.initializeBuysOptions()
        }
        if (PaymentsType.isBill(paymentsType)) {
            MenuHelper.initializeBillsOptions()
        }
        val anyActive = databaseManager?.anyActivePaymentsRecordsBy(paymentsType)
        titleImageView?.setImageResource(if (anyActive == true) R.drawable.ic_red_money else R.drawable.ic_menu_green_money)
        subTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        context?.let {
            subTitleTextView?.setTextColor(
                if (anyActive == true) it.getColor(R.color.colorRed) else it.getColor(
                    R.color.colorPrimary
                )
            )
        }
        subTitleTextView?.text = adapter?.totalPrice.toString()
        checker?.isChecked = databaseManager?.allActivePaymentsRecordsBy(paymentsType) == true
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        val paymentsFilter = adapter?.filter
        paymentsFilter?.filter(queryStr)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onDetailsActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.DETAIL_REQUEST_CODE) {
            resultData?.let { intent ->
                val entity: Any? = intent.getSerializableExtra(Constants.ENTITY)
                val payment = entity?.let { ParserUtils.toPayments(it) }
                payment?.let { adapter?.notifyItemAddedOrChanged(it) }
            }
        }
    }

    private fun importExportMenuItemClickListener() = DialogUtils.showImportExportDialog(
        context, R.string.import_export_title,
        this::importMenuItemClickListener,
        this::exportMenuItemClickListener
    )

    private fun deleteAllMenuItemClickListener() = DialogUtils.confirmationDialog(
        requireContext(), R.string.delete_all_records_title, R.string.delete_all_records_message
    ) { deleteAllPayments() }


    private fun restoreDefaultMenuItemClickListener() = DialogUtils.confirmationDialog(
        requireContext(),
        R.string.restore_default_records_title,
        R.string.restore_default_records_message
    ) { restoreDefaultPayments() }
}