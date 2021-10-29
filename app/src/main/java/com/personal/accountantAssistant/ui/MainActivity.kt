package com.personal.accountantAssistant.ui

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.ActivityMainBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.ui.adapters.ViewPagerAdapter
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.utils.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val icons = arrayOf(
        R.drawable.ic_menu_home, R.drawable.ic_menu_buys, R.drawable.ic_menu_bills
    )
    private val titles = arrayOf(R.string.menu_home, R.string.menu_buys, R.string.menu_bills)
    private var tabLayoutMediator: TabLayoutMediator? = null

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

        vpContent.adapter = ViewPagerAdapter(
            this@MainActivity, listOf(
                HomeFragment.newInstance(), BuysFragment.newInstance(), BillsFragment.newInstance()
            )
        )
        tabLayoutMediator = TabLayoutMediator(tabHeader, vpContent) { tab, index ->
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