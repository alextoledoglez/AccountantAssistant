package com.personal.accountantAssistant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.databinding.ActivityMainBinding
import com.personal.accountantAssistant.di.MainModuleInitializer
import com.personal.accountantAssistant.ui.adapters.ViewPagerAdapter
import com.personal.accountantAssistant.ui.bills.BillsFragment
import com.personal.accountantAssistant.ui.buys.BuysFragment
import com.personal.accountantAssistant.ui.home.HomeFragment
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var mainContext: Context
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: ActivityMainBinding
    private var databaseManager: DatabaseManager? = null

    private val icons = arrayOf(R.drawable.ic_menu_home, R.drawable.ic_menu_buys, R.drawable.ic_menu_bills)
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

        mainContext = this@MainActivity
        mainActivity = this@MainActivity
        databaseManager = DatabaseManager(applicationContext)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        vpContent.adapter = ViewPagerAdapter(this@MainActivity, listOf(
                HomeFragment.newInstance(), BuysFragment.newInstance(), BillsFragment.newInstance()
        ))
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
        MenuHelper.enableMenuItemOptions(java.lang.Boolean.FALSE)
        return java.lang.Boolean.TRUE
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.add_payment -> addMenuItemClickListener()
            R.id.import_export -> importExportMenuItemClickListener()
            R.id.delete_all -> deleteAllMenuItemClickListener()
            R.id.restore_default -> restoreDefaultMenuItemClickListener()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

    private fun addBuys() {
        ActivityUtils.startPaymentDetailsActivity(mainContext, PaymentsType.BUY)
    }

    private fun addBills() {
        ActivityUtils.startPaymentDetailsActivity(mainContext, PaymentsType.BILL)
    }

    private fun importBuys() {
        ImportExportUtils.xlsImport(mainContext, PaymentsType.BUY)
    }

    private fun importBills() {
        ImportExportUtils.xlsImport(mainContext, PaymentsType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun exportBuys() {
        ImportExportUtils.xlsExport(mainContext, PaymentsType.BUY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun exportBills() {
        ImportExportUtils.xlsExport(mainContext, PaymentsType.BILL)
    }

    private fun deleteAllBuysRecords(): Boolean? {
        return databaseManager?.deleteAllBuysRecord()?.let { DataBaseUtils.isNotDefaultRecord(it) }
    }

    private fun deleteAllBillsRecords(): Boolean? {
        return databaseManager?.deleteAllBillsRecord()?.let { DataBaseUtils.isNotDefaultRecord(it) }
    }

    private fun deleteAllBuys() {
        deleteAllBuysRecords()?.let {
            ActionUtils.conditionalActions(it, { refreshRecyclerView() })
        }
    }

    private fun deleteAllBills() {
        deleteAllBillsRecords()?.let {
            ActionUtils.conditionalActions(it, { refreshRecyclerView() })
        }
    }

    private fun restoreDefaultBuys() {
        deleteAllBuysRecords()?.let {
            databaseManager?.insertDefaultBuysRecords()
            refreshRecyclerView()
        }
    }

    private fun restoreDefaultBills() {
        deleteAllBillsRecords()?.let {
            databaseManager?.insertDefaultBillsRecords()
            refreshRecyclerView()
        }
    }

    private fun refreshRecyclerView() {
        ActivityUtils.refreshBy(mainContext)
    }

    private fun addMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ addBuys() }, { addBills() })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun importExportMenuItemClickListener() {
        DialogUtils.showImportExportDialog(
                mainContext,
                R.string.import_export_title,
                { importMenuItemClickListener() },
                { exportMenuItemClickListener() }
        )
    }

    private fun importMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ importBuys() }, { importBills() })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun exportMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ exportBuys() }, { exportBills() })
    }

    private fun deleteAllMenuItemClickListener() {
        DialogUtils.confirmationDialog(
                mainContext,
                R.string.delete_all_records_title,
                R.string.delete_all_records_message
        ) { MenuHelper.conditionalMenuItemClickListener({ deleteAllBuys() }, { deleteAllBills() }) }
    }

    private fun restoreDefaultMenuItemClickListener() {
        DialogUtils.confirmationDialog(
                mainContext,
                R.string.restore_default_records_title,
                R.string.restore_default_records_message
        ) {
            MenuHelper.conditionalMenuItemClickListener(
                    { restoreDefaultBuys() },
                    { restoreDefaultBills() }
            )
        }
    }
}