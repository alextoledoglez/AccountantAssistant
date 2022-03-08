package com.personal.accountantAssistant.ui

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.ViewPagerAdapter
import com.personal.accountantAssistant.databinding.ActivityMainBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.wallet.WalletFragment
import com.personal.accountantAssistant.utils.MenuHelper
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var tabLayoutMediator: TabLayoutMediator? = null

    private val icons = arrayOf(
        R.drawable.ic_home, R.drawable.ic_wallet, R.drawable.ic_buys, R.drawable.ic_bills
    )

    private val titles = arrayOf(
        R.string.menu_home, R.string.menu_wallet, R.string.menu_buys, R.string.menu_bills
    )

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator?.detach()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainModuleInitializer.initialize()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.vpContent.adapter = ViewPagerAdapter(
            this@MainActivity, listOf(
                HomeFragment.newInstance(),
                WalletFragment.newInstance(),
                BuysFragment.newInstance(),
                BillsFragment.newInstance()
            )
        )
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