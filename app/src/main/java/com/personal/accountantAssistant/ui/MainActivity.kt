package com.personal.accountantAssistant.ui

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.wallet.WalletFragment
import com.personal.accountantAssistant.utils.MenuHelper
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var pagerAdapter: ViewPagerAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    private val icons = arrayOf(
        R.drawable.ic_home, R.drawable.ic_wallet, R.drawable.ic_buys, R.drawable.ic_bills
    )

    private val titles = arrayOf(
        R.string.menu_home, R.string.menu_wallet, R.string.menu_buys, R.string.menu_bills
    )

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val page: Fragment? = pagerAdapter?.fragments?.get(position)
            binding.fabAdd.apply {
                isVisible = true
                when (page) {
                    is WalletFragment -> setOnClickListener { page.onItemClick(CardModel()) }
                    is BuysFragment -> setOnClickListener { page.onItemClick(ExpenseModel().toBuy()) }
                    is BillsFragment -> setOnClickListener { page.onItemClick(ExpenseModel().toBill()) }
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

        tabLayoutMediator = TabLayoutMediator(binding.tabHeader, binding.vpContent) { tab, index ->
            tab.apply {
                setIcon(icons[index])
                setText(titles[index])
            }
        }
        tabLayoutMediator?.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        MenuHelper.mainMenu = menu
        MenuHelper.enableMenuItemOptions(false)
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

}