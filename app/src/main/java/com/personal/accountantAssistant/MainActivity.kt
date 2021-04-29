package com.personal.accountantAssistant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.personal.accountantAssistant.db.DatabaseManager
import com.personal.accountantAssistant.db.LocalStorage
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.*

class MainActivity : AppCompatActivity() {

    private var context: Context? = null
    private lateinit var mainActivity: MainActivity
    private var databaseManager: DatabaseManager? = null
    private var mAppBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext
        mainActivity = this@MainActivity
        databaseManager = DatabaseManager(context)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        mAppBarConfiguration = AppBarConfiguration.Builder(R.id.nav_home,
                R.id.nav_buys,
                R.id.nav_bills /*,
                R.id.nav_tickets,
                R.id.nav_remittances*/)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(mainActivity, R.id.nav_host_fragment)
        mAppBarConfiguration?.let {
            NavigationUI.setupActionBarWithNavController(mainActivity, navController, it)
        }
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        MenuHelper.setMainMenu(menu)
        MenuHelper.enableMenuItemOptions(java.lang.Boolean.FALSE)
        return java.lang.Boolean.TRUE
    }

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(mainActivity, R.id.nav_host_fragment)
        val isNavigateUp = mAppBarConfiguration?.let { NavigationUI.navigateUp(navController, it) }
        return (isNavigateUp == true || super.onSupportNavigateUp())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.primaryNavigationFragment?.let {
            it.childFragmentManager.fragments.forEach { fragment ->
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun addBuys() {
        ActivityUtils.startPaymentDetailsActivity(context, PaymentsType.BUY)
    }

    private fun addBills() {
        ActivityUtils.startPaymentDetailsActivity(context, PaymentsType.BILL)
    }

    private fun importBuys() {
        ImportExportUtils.xlsImport(context, PaymentsType.BUY)
    }

    private fun importBills() {
        ImportExportUtils.xlsImport(context, PaymentsType.BILL)
    }

    private fun exportBuys() {
        ImportExportUtils.xlsExport(context, PaymentsType.BUY)
    }

    private fun exportBills() {
        ImportExportUtils.xlsExport(context, PaymentsType.BILL)
    }

    private fun deleteAllBuysRecords(): Boolean? {
        return databaseManager?.deleteAllBuysRecord()?.let { DataBaseUtils.isNotDefaultRecord(it) }
    }

    private fun deleteAllBillsRecords(): Boolean? {
        return databaseManager?.deleteAllBillsRecord()?.let { DataBaseUtils.isNotDefaultRecord(it) }
    }

    private fun deleteAllBuys() {
        deleteAllBuysRecords()?.let {
            ActionUtils.conditionalActions(it, { refreshRecyclerView() }, ActionUtils.NONE_ACTION_TO_DO)
        }
    }

    private fun deleteAllBills() {
        deleteAllBillsRecords()?.let {
            ActionUtils.conditionalActions(it, { refreshRecyclerView() }, ActionUtils.NONE_ACTION_TO_DO)
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
        ActivityUtils.refreshBy(context)
    }

    private fun addMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ addBuys() }) { addBills() }
    }

    private fun importExportMenuItemClickListener() {
        DialogUtils.showImportExportDialog(context, R.string.import_export_title, { importMenuItemClickListener() }) { exportMenuItemClickListener() }
    }

    private fun importMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ importBuys() }) { importBills() }
    }

    private fun exportMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener({ exportBuys() }) { exportBills() }
    }

    private fun deleteAllMenuItemClickListener() {
        DialogUtils.confirmationDialog(context,
                R.string.delete_all_records_title,
                R.string.delete_all_records_message
        ) { MenuHelper.conditionalMenuItemClickListener({ deleteAllBuys() }) { deleteAllBills() } }
    }

    private fun restoreDefaultMenuItemClickListener() {
        DialogUtils.confirmationDialog(context,
                R.string.restore_default_records_title,
                R.string.restore_default_records_message
        ) { MenuHelper.conditionalMenuItemClickListener({ restoreDefaultBuys() }) { restoreDefaultBills() } }
    }
}