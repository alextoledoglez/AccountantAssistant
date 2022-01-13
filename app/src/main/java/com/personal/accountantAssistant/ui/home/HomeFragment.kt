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
import com.personal.accountantAssistant.databinding.LayoutHomeCardBinding
import com.personal.accountantAssistant.domain.models.TextSizeResourcesModel
import com.personal.accountantAssistant.domain.models.home.ColorResourcesModel
import com.personal.accountantAssistant.domain.models.home.SummaryItemModel
import com.personal.accountantAssistant.domain.models.home.TitleResourcesModel
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
            expensesValues.observe(viewLifecycleOwner, { expenses ->
                setupExpenseCard(R.id.daily_card, expenses.daily)
                setupExpenseCard(R.id.buy_card, expenses.buys)
                setupExpenseCard(R.id.bill_card, expenses.bills)
            })
            totalExpensesValue.observe(viewLifecycleOwner, ::highlightExpenses)
            summaryValues.observe(viewLifecycleOwner, { summary ->
                setupSummaryCard(binding.availableSection.availableCard, summary?.available)
                setupSummaryCard(binding.summarySection.neededCard, summary?.gainOrNeeded)
                setupSummaryCard(binding.summarySection.totalCard, summary?.expenses)
            })
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

    private fun setupExpenseCard(resourceId: Int, cardTextValue: Double?) {

        val rootView = binding.root.findViewById<View>(resourceId)
        val imageView = rootView.ivCardImage
        val titleTextView = rootView.tvCardTitle
        val subtitleTextView = rootView.tvCardSubtitle
        val isExpensesMoreThanAvailable = viewModel.isExpensesMoreThanAvailable(cardTextValue)

        cardTitle = String.EMPTY
        textSize = textSizeResources.normal
        fontColorResource = getColorResourceBy(
            isExpensesMoreThanAvailable, colorResources.error, colorResources.success
        )
        colorResource = getDefaultBackgroundColorResource()

        when (resourceId) {
            R.id.daily_card -> setupImageCardTitleBy(
                titleResources.daily,
                isExpensesMoreThanAvailable,
                R.drawable.ic_menu_red_daily,
                R.drawable.ic_menu_green_daily
            )
            R.id.buy_card -> setupImageCardTitleBy(
                titleResources.buys,
                isExpensesMoreThanAvailable,
                R.drawable.ic_menu_red_buys,
                R.drawable.ic_menu_green_buys
            )
            R.id.bill_card -> setupImageCardTitleBy(
                titleResources.bills,
                isExpensesMoreThanAvailable,
                R.drawable.ic_menu_red_bills,
                R.drawable.ic_menu_green_bills
            )
        }

        setupCardImageView(imageView)
        setupCardTitleTextView(titleTextView)
        setupCardSubtitleTextView(cardTextValue.toString(), subtitleTextView)
    }

    private fun setupSummaryCard(layout: LayoutHomeCardBinding, model: SummaryItemModel?) =
        layout.apply {
            val notNullColor = model?.colorResource ?: R.color.colorBlack
            ivCardImage.visibility = View.GONE

            tvCardTitle.text = model?.strResource?.let { getString(it) }
            tvCardTitle.setTextColor(tvCardTitle.context.getColor(notNullColor))
            tvCardTitle.visibility = View.VISIBLE

            tvCardSubtitle.text = model?.value?.orZero().toString()
            tvCardSubtitle.setTextColor(tvCardSubtitle.context.getColor(notNullColor))
            tvCardSubtitle.visibility = View.VISIBLE
        }

    private fun setupCardImageView(cardImageView: ImageView) = cardImageView.apply {
        imageResource?.let { setImageResource(it) }
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

    private fun setupImageCardTitleBy(
        @StringRes titleRes: Int,
        isMoreThanAvailable: Boolean?,
        @DrawableRes warningResource: Int,
        @DrawableRes successResource: Int
    ) {
        cardTitle = getString(titleRes)
        imageResource = if (isMoreThanAvailable == true) warningResource else successResource
        hideImageView = false
        colorResource = getDefaultBackgroundColorResource()
    }

    private fun highlightExpenses(expenses: Double?) {

        val totalExpenses = expenses.orZero()
        val availableMoney = viewModel.availableMoney.orZero()

        val isExpensesLessThanAvailable = viewModel.isExpensesLessThanAvailable(totalExpenses)
        val availableColor = getColorResourceBy(
            isExpensesLessThanAvailable, colorResources.success, colorResources.error
        )
        val available = SummaryItemModel(
            availableMoney.toFloat(), titleResources.available, availableColor
        )

        val isExpensesMoreThanAvailable = viewModel.isExpensesMoreThanAvailable(totalExpenses)
        val totalColor = getColorResourceBy(
            isExpensesMoreThanAvailable, colorResources.error, colorResources.success
        )
        val total = SummaryItemModel(totalExpenses.toFloat(), titleResources.total, totalColor)

        val gainOrNeededValue = NumberUtils.roundTo(availableMoney.minus(totalExpenses))
        val isZeroLessThanGainOrNeeded = viewModel.isZeroLessThan(gainOrNeededValue)
        val gainOrNeededTitle =
            if (isZeroLessThanGainOrNeeded) titleResources.gain else titleResources.missing
        val gainOrNeededColor = getColorResourceBy(
            isZeroLessThanGainOrNeeded, colorResources.success, colorResources.error
        )

        val gainOrNeeded = SummaryItemModel(
            gainOrNeededValue.toFloat(), gainOrNeededTitle, gainOrNeededColor
        )

        viewModel.setHomeSummaryModel(available, total, gainOrNeeded)
    }

    private fun getColorResourceBy(
        isCondition: Boolean?, @ColorRes trueResource: Int, @ColorRes falseResource: Int
    ) = if (isCondition == true) trueResource else falseResource

    companion object {
        fun newInstance() = HomeFragment()
    }
}