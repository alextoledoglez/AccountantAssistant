package com.personal.accountantAssistant.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.ViewPagerAdapter
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.databinding.ActivityMainBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.hideMenuOptions
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.wallet.WalletFragment
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var pagerAdapter: ViewPagerAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val page: Fragment? = pagerAdapter?.fragments?.get(position)
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
        binding.vpContent.unregisterOnPageChangeCallback(pageChangeCallback)
        tabLayoutMediator?.detach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vpContent.apply {
            registerOnPageChangeCallback(pageChangeCallback)
            pagerAdapter = ViewPagerAdapter(
                this@MainActivity, listOf(
                    HomeFragment.newInstance(),
                    WalletFragment.newInstance(),
                    BuysFragment.newInstance(),
                    BillsFragment.newInstance()
                )
            )
            adapter = pagerAdapter
        }

        val icons = resources.obtainTypedArray(R.array.tabs_icons)
        val titles = resources.getStringArray(R.array.tabs_titles)
        tabLayoutMediator = TabLayoutMediator(binding.tabHeader, binding.vpContent) { tab, index ->
            tab.apply {
                setIcon(icons.getResourceId(index, -1))
                text = titles[index]
            }
        }
        tabLayoutMediator?.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.hideMenuOptions()
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

}