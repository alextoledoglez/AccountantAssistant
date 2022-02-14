package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.domain.models.TextSizeResourcesModel
import com.personal.accountantAssistant.domain.models.home.*
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.utils.MenuHelper
import com.personal.accountantAssistant.utils.NumberUtils
import kotlinx.android.synthetic.main.layout_home_card.view.*
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.P)
class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)
    private var textSizeResources: TextSizeResourcesModel = TextSizeResourcesModel()
    private var titleResources: TitleResourcesModel = TitleResourcesModel()
    private var colorResources: ColorResourcesModel = ColorResourcesModel()
    private var cardTitle: String? = String.EMPTY
    private var hideImageView: Boolean? = false
    private var imageResource: Int? = null
    private var textSize: Int? = null

    @ColorRes
    private var fontColorResource = R.color.colorBlack

    @ColorRes
    private var colorResource = getDefaultBackgroundColorResource()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        viewModel.calculateExpenses()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        MenuHelper.initializeHomeOptions()
        with(viewModel) {
            dashboardValues.observe(viewLifecycleOwner) {
                setupDashboardCard(R.id.available_card, it?.available)
                setupDashboardCard(R.id.buy_card, it?.expensesItems?.buy)
                setupDashboardCard(R.id.bill_card, it?.expensesItems?.bill)
                setupDashboardCard(R.id.needed_card, it?.gainOrNeeded)
                setupDashboardCard(R.id.total_card, it?.expensesItems?.total)
            }
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
        }
        with(binding) {
            viewModel.calculateExpenses()
            mbDateRangePicker.setOnClickListener { showRangePicker() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_period))
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.savePeriodDates(it?.first, it?.second)
                    viewModel.calculateExpenses()
                }
            }
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    private fun getDefaultBackgroundColorResource() = R.color.colorWhite

    private fun setupDashboardCard(resourceId: Int, model: DashboardItemModel?) {

        val rootView = binding.root.findViewById<View>(resourceId)
        val imageView = rootView.ivCardImage
        val titleTextView = rootView.tvCardTitle
        val subtitleTextView = rootView.tvCardSubtitle

        cardTitle = String.EMPTY
        textSize = textSizeResources.normal
        model?.colorResource?.let { fontColorResource = it }

        when (resourceId) {
            R.id.available_card -> setupImageCardTitleBy(model?.strResource, R.drawable.ic_wallet)
            R.id.needed_card -> setupImageCardTitleBy(model?.strResource, R.drawable.ic_money)
            R.id.total_card -> setupImageCardTitleBy(model?.strResource, R.drawable.ic_total)
            R.id.buy_card -> setupImageCardTitleBy(model?.strResource, R.drawable.ic_buys)
            R.id.bill_card -> setupImageCardTitleBy(model?.strResource, R.drawable.ic_bills)
        }

        setupCardImageView(imageView)
        setupCardTitleTextView(titleTextView)
        setupCardSubtitleTextView(model?.value.toString(), subtitleTextView)
    }

    private fun setupCardImageView(cardImageView: ImageView) = cardImageView.apply {
        imageResource?.let { setImageResource(it) }
        setColorFilter(context.getColor(fontColorResource), android.graphics.PorterDuff.Mode.SRC_IN)
        visibility = if (hideImageView == true) View.GONE else View.VISIBLE
    }

    private fun setupCardTitleTextView(tvTitle: TextView) = tvTitle.apply {
        text = cardTitle.orEmpty()
        setTextColor(context.getColor(fontColorResource))
        setBackgroundColor(context.getColor(colorResource))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.orZero())
    }

    private fun setupCardSubtitleTextView(value: String, tvSubtitle: TextView) = tvSubtitle.apply {
        text = abs(value.toDouble()).toString()
        setTextColor(context.getColor(fontColorResource))
    }

    private fun setupImageCardTitleBy(@StringRes titleRes: Int?, @DrawableRes imageResource: Int?) {
        hideImageView = false
        this.imageResource = imageResource
        cardTitle = titleRes?.let { getString(it) }
        colorResource = getDefaultBackgroundColorResource()
    }

    private fun settingDashboardItems(values: ExpensesValuesModel?) {

        val availableMoney = viewModel.availableMoney.orZero()
        val buyExpenses = values?.buys.orZero()
        val billExpenses = values?.bills.orZero()
        val totalExpenses = NumberUtils.roundTo(values?.total.orZero())
        val gainOrNeededValue = NumberUtils.roundTo(availableMoney.minus(totalExpenses))

        val isExpensesLessThanAvailable = viewModel.isExpensesLessThanAvailable(totalExpenses)
        val availableColor = getColorResourceBy(isExpensesLessThanAvailable)
        val available = DashboardItemModel(availableMoney, titleResources.available, availableColor)

        val expenses = ExpensesItemsModel(
            DashboardItemModel(
                buyExpenses, titleResources.buys, getExpenseColorResourceBy(buyExpenses)
            ),
            DashboardItemModel(
                billExpenses, titleResources.bills, getExpenseColorResourceBy(billExpenses)
            ),
            DashboardItemModel(
                totalExpenses, titleResources.total, getExpenseColorResourceBy(totalExpenses)
            )
        )

        val isZeroLessThanGainOrNeeded = viewModel.isZeroLessThan(gainOrNeededValue)
        val gainOrNeededTitle =
            if (isZeroLessThanGainOrNeeded) titleResources.gain else titleResources.missing
        val gainOrNeededColor = getColorResourceBy(isZeroLessThanGainOrNeeded)
        val gainOrNeeded =
            DashboardItemModel(gainOrNeededValue, gainOrNeededTitle, gainOrNeededColor)

        viewModel.postDashboardValues(available, expenses, gainOrNeeded)
    }

    private fun getColorResourceBy(
        isCondition: Boolean?,
        @ColorRes trueResource: Int = colorResources.success,
        @ColorRes falseResource: Int = colorResources.error
    ) = if (isCondition == true) trueResource else falseResource

    private fun getExpenseColorResourceBy(expense: Double?): Int = getColorResourceBy(
        viewModel.isExpensesMoreThanAvailable(expense),
        colorResources.error,
        colorResources.success
    )

    companion object {
        fun newInstance() = HomeFragment()
    }
}