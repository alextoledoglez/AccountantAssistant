package com.personal.accountantAssistant.ui

import android.os.Bundle
import android.view.Menu
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
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
import com.personal.accountantAssistant.extensions.hideMenuOptions
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.menu.MenuFragment
import com.personal.accountantAssistant.ui.wallet.WalletFragment

class MainActivity : BaseActivity<Nothing>() {

    override val binding by viewBinding(ActivityMainBinding::inflate)
    private val tabLayout by lazy { binding.tabLayout }
    private val vpContent by lazy { binding.vpContent }
    private var tlMediator: TabLayoutMediator? = null
    private val pagerAdapter: ViewPagerAdapter? by lazy {
        ViewPagerAdapter(
            this@MainActivity, listOf(
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
            binding.fabAdd.apply {
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

    override fun onDestroy() {
        super.onDestroy()
        vpContent.unregisterOnPageChangeCallback(pageChangeCallback)
        tlMediator?.detach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        vpContent.registerOnPageChangeCallback(pageChangeCallback)
        vpContent.adapter = pagerAdapter

        val tabIcons = resources.obtainTypedArray(R.array.tabs_icons)
        tlMediator = TabLayoutMediator(tabLayout, vpContent) { tab, index ->
            tab.setIcon(tabIcons.getResourceIdOrThrow(index))
        }.apply {
            attach()
            tabIcons.recycle()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.hideMenuOptions()
        return true
    }

    override fun onBackPressed() {
        closeApp()
    }

    fun navigateToTab(tabPosition: TabPositions) {
        tabLayout.apply { selectTab(getTabAt(tabPosition.position)) }
    }
}