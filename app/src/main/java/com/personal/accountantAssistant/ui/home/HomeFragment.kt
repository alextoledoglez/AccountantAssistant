package com.personal.accountantAssistant.ui.home

import android.view.Menu
import android.view.MenuInflater
import androidx.compose.runtime.Composable
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AdProvider
import com.personal.accountantAssistant.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private val adProvider: AdProvider? by inject()

    override fun onDestroy() {
        super.onDestroy()
        adProvider?.destroyAd()
    }

    override fun onPause() {
        super.onPause()
        adProvider?.pauseAd()
    }

    override fun onResume() {
        super.onResume()
        adProvider?.resumeAd()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        setHasOptionsMenu(true)
    }

    override fun initObservers() {
        viewModel.loadPeriodDates()
        viewModel.loadAvailableMoney()
    }

    @Composable
    override fun ScreenContent() {
        HomeScreen(
            viewModel = viewModel,
            adProvider = adProvider,
            onShowDatePicker = ::showRangePicker,
            onItemClick = ::onItemClickListener
        )
    }

    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_period))
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener { period ->
                    viewModel.savePeriodDates(period)
                    viewModel.loadPeriodDates()
                    viewModel.loadAvailableMoney()
                }
            }
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    private fun onItemClickListener(drawableRes: Int) {
        val mainActivity = activity as? MainActivity
        when (drawableRes) {
            R.drawable.ic_buys -> mainActivity?.navigateToTab(TabPositions.BUYS)
            R.drawable.ic_bills -> mainActivity?.navigateToTab(TabPositions.BILLS)
            else -> mainActivity?.navigateToTab(TabPositions.WALLET)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}