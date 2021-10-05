package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.personal.accountantAssistant.core.BaseFragment
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.utils.DatePickerDialogUtils
import com.personal.accountantAssistant.utils.EditableTextsUtils
import com.personal.accountantAssistant.utils.MenuHelper
import kotlinx.android.synthetic.main.dashboard_editable_card.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)


    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        MenuHelper.initializeHomeOptions()
        with(viewModel) {
            availableMoney.observe(viewLifecycleOwner, { updateAvailableMoney(it) })
            periodText.observe(viewLifecycleOwner, {
                it?.let { value -> period_card_text_view.text = value }
            })
            viewModel.setAvailableMoney()
        }
        with(binding) {
            calculateExpensesOn(root)
            card_edit_text?.apply {
                setText(viewModel.getAvailableMoneyStr())
                doOnTextChanged { text, _, _, _ ->
                    viewModel.updateAvailableMoney(text.toString())
                    calculateExpensesOn(root)
                }
            }
            DatePickerDialogUtils.initializeCalendarPickerView(periodCalendarPickerView, {
/*                periodCard.subTitleTextView.text = DateUtils.toPeriodStr(
                    localStorage?.getFirstDate(),
                    localStorage?.getLastDate()
                )*/
            }) {
                EditableTextsUtils.hideSoftInputFromWindow(activity, card_edit_text)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpensesOn(rootView: View) =
        viewModel.calculateExpenses(context, activity, rootView)
}