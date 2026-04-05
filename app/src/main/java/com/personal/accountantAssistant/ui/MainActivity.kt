package com.personal.accountantAssistant.ui

import android.os.Bundle
import android.view.Menu
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseActivity
import com.personal.accountantAssistant.bases.adapters.ViewPagerAdapter
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.databinding.ActivityMainBinding
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.closeApp
import com.personal.accountantAssistant.extensions.getSystemBars
import com.personal.accountantAssistant.extensions.hideMenuOptions
import com.personal.accountantAssistant.extensions.setBottomPadding
import com.personal.accountantAssistant.extensions.setTopPadding
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.menu.MenuFragment
import com.personal.accountantAssistant.ui.wallet.WalletFragment
import kotlin.collections.listOf

class MainActivity : BaseActivity() {

    override val binding by viewBinding(ActivityMainBinding::inflate)
    private val vpContent by lazy { binding.vpContent }
    private val fabAdd by lazy { binding.fabAdd }
    private val tabLayout by lazy { binding.tabLayout }
    private var tlMediator: TabLayoutMediator? = null
    private val pagerAdapter: ViewPagerAdapter? by lazy {
        ViewPagerAdapter(
            frag = this@MainActivity,
            fragments = listOf(
                HomeFragment.newInstance(),
                WalletFragment.newInstance(),
                BuysFragment.newInstance(),
                BillsFragment.newInstance(),
                MenuFragment.newInstance()
            )
        )
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val page = pagerAdapter?.fragments?.get(position)
            fabAdd.apply {
                isVisible = true
                when (page) {
                    is WalletFragment -> setOnClickListener { page.onEditCard(CardModel()) }
                    is BuysFragment -> setOnClickListener { page.onEditBuy(ExpenseModel().toBuy()) }
                    is BillsFragment -> setOnClickListener { page.onEditBill(ExpenseModel().toBill()) }
                    else -> isVisible = false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setViewPagerContent()
        setTabLayoutContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.hideMenuOptions()
        return true
    }

    override fun onBackPressed() {
        closeApp()
    }

    override fun onDestroy() {
        super.onDestroy()
        vpContent.unregisterOnPageChangeCallback(pageChangeCallback)
        tlMediator?.detach()
        tlMediator = null
    }

    private fun setViewPagerContent() {
        vpContent.registerOnPageChangeCallback(pageChangeCallback)
        vpContent.adapter = pagerAdapter
        ViewCompat.setOnApplyWindowInsetsListener(vpContent) { view, insets ->
            view.setTopPadding(topPadding = insets.getSystemBars().top)
            insets
        }
    }

    private fun setTabLayoutContent() {
        val tabIcons = resources.obtainTypedArray(R.array.tabs_icons)
        tlMediator = TabLayoutMediator(tabLayout, vpContent) { tab, index ->
            tab.setIcon(tabIcons.getResourceIdOrThrow(index))
        }.apply {
            attach()
            tabIcons.recycle()
        }
        ViewCompat.setOnApplyWindowInsetsListener(tabLayout) { view, insets ->
            view.setBottomPadding(bottomPadding = insets.getSystemBars().bottom)
            insets
        }
    }

    fun navigateToTab(tabPosition: TabPositions) {
        tabLayout.selectTab(tabLayout.getTabAt(tabPosition.position))
    }
}