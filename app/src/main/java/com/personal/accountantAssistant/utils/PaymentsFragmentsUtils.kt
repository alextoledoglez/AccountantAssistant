package com.personal.accountantAssistant.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.payments.PaymentsListAdapter
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.ActionUtils.runAction
import com.personal.accountantAssistant.utils.DataBaseUtils.allActivePaymentsRecordsBy
import com.personal.accountantAssistant.utils.DataBaseUtils.anyActivePaymentsRecordsBy
import com.personal.accountantAssistant.utils.MenuHelper.initializeBillsOptions
import com.personal.accountantAssistant.utils.MenuHelper.initializeBuysOptions
import com.personal.accountantAssistant.utils.ParserUtils.isNullObject
import com.personal.accountantAssistant.utils.ParserUtils.toPayments
import io.reactivex.functions.Action
import java.util.*

@RequiresApi(Build.VERSION_CODES.P)
class PaymentsFragmentsUtils(val context: Context?,
                             private val activity: Activity?,
                             private val paymentsType: PaymentsType) {
    private var titleImageView: ImageView? = null
    private var subTitleTextView: TextView? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var checker: androidx.appcompat.widget.SwitchCompat? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: PaymentsListAdapter? = null

    private fun initializeAdapter(onChangeAction: Action?) {
        adapter = context?.let { PaymentsListAdapter(it, paymentsType) }
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                onChangeAction?.let { runAction(it) }
            }
        })
    }

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
        checker?.setOnClickListener { checker?.isChecked?.let { adapter?.setAllPaymentsRecordsActiveFrom(it) } }

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
        initializeAdapter(Action { updateRecyclerView() })

        //Recycler View
        if (PaymentsType.isBuy(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.buy_list)
        }
        if (PaymentsType.isBill(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.bills_list)
        }
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        updateRecyclerView()
    }

    fun getTotalPriceUntil(lastPeriodDate: Date?): Double? {
        if (isNullObject(adapter)) {
            initializeAdapter(ActionUtils.NONE_ACTION_TO_DO)
        }
        return adapter?.getTotalPriceUntil(lastPeriodDate)
    }

    private fun updateRecyclerView() {
        if (PaymentsType.isBuy(paymentsType)) {
            initializeBuysOptions()
        }
        if (PaymentsType.isBill(paymentsType)) {
            initializeBillsOptions()
        }
        val anyActive = anyActivePaymentsRecordsBy(activity, paymentsType)
        titleImageView?.setImageResource(if (anyActive == true) R.drawable.ic_red_money else R.drawable.ic_menu_green_money)
        subTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        context?.let {
            subTitleTextView?.setTextColor(if (anyActive == true) it.getColor(R.color.colorRed) else it.getColor(R.color.colorPrimary))
        }
        subTitleTextView?.text = adapter?.totalPrice.toString()
        checker?.isChecked = allActivePaymentsRecordsBy(activity, paymentsType) == true
    }

    private fun recyclerViewAdapterFilterBy(queryStr: String) {
        val paymentsFilter = adapter?.filter
        paymentsFilter?.filter(queryStr)
    }

    fun onDetailsActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.DETAIL_REQUEST_CODE) {
            resultData?.let { intent ->
                val entity: Any? = intent.getSerializableExtra(Constants.ENTITY)
                val payment = entity?.let { toPayments(it) }
                payment?.let { adapter?.notifyItemAddedOrChanged(it) }
            }
        }
    }
}