package com.personal.accountantAssistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.personal.accountantAssistant.db.DatabaseManager;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.ActionUtils;
import com.personal.accountantAssistant.utils.ActivityUtils;
import com.personal.accountantAssistant.utils.DataBaseUtils;
import com.personal.accountantAssistant.utils.DialogUtils;
import com.personal.accountantAssistant.utils.ImportExportUtils;
import com.personal.accountantAssistant.utils.MenuHelper;
import com.personal.accountantAssistant.utils.ParserUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static Context context;
    private DatabaseManager databaseManager;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        databaseManager = new DatabaseManager(context);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
                R.id.nav_buys,
                R.id.nav_bills/*,
                R.id.nav_tickets,
                R.id.nav_remittances*/)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuHelper.setMainMenu(menu);
        MenuHelper.enableMenuItemOptions(Boolean.FALSE);
        return Boolean.TRUE;
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add_payment:
                addMenuItemClickListener();
                break;
            case R.id.import_export:
                importExportMenuItemClickListener();
                break;
            case R.id.delete_all:
                deleteAllMenuItemClickListener();
                break;
            case R.id.restore_default:
                restoreDefaultMenuItemClickListener();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        if (ParserUtils.isNotNullObject(primaryNavigationFragment)) {
            for (Fragment fragment : primaryNavigationFragment.getChildFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void addBuys() {
        ActivityUtils.startPaymentDetailsActivity(context, PaymentsType.BUY);
    }

    private void addBills() {
        ActivityUtils.startPaymentDetailsActivity(context, PaymentsType.BILL);
    }

    private void importBuys() {
        ImportExportUtils.xlsImport(context, PaymentsType.BUY);
    }

    private void importBills() {
        ImportExportUtils.xlsImport(context, PaymentsType.BILL);
    }

    private void exportBuys() {
        ImportExportUtils.xlsExport(context, PaymentsType.BUY);
    }

    private void exportBills() {
        ImportExportUtils.xlsExport(context, PaymentsType.BILL);
    }

    private boolean deleteAllBuysRecords() {
        return (DataBaseUtils.isNotDefaultRecord(databaseManager.deleteAllBuysRecord()));
    }

    private boolean deleteAllBillsRecords() {
        return (DataBaseUtils.isNotDefaultRecord(databaseManager.deleteAllBillsRecord()));
    }

    private void deleteAllBuys() {
        ActionUtils.conditionalActions(deleteAllBuysRecords(),
                this::refreshRecyclerView,
                ActionUtils.NONE_ACTION_TO_DO);
    }

    private void deleteAllBills() {
        ActionUtils.conditionalActions(deleteAllBillsRecords(),
                this::refreshRecyclerView,
                ActionUtils.NONE_ACTION_TO_DO);
    }

    private void restoreDefaultBuys() {
        if (deleteAllBuysRecords()) {
            databaseManager.insertDefaultBuysRecords();
            refreshRecyclerView();
        }
    }

    private void restoreDefaultBills() {
        if (deleteAllBillsRecords()) {
            databaseManager.insertDefaultBillsRecords();
            refreshRecyclerView();
        }
    }

    private void refreshRecyclerView() {
        ActivityUtils.refreshBy(getContext());
    }

    private void addMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener(this::addBuys, this::addBills);
    }

    private void importExportMenuItemClickListener() {
        DialogUtils.showImportExportDialog(context,
                R.string.import_export_title,
                this::importMenuItemClickListener,
                this::exportMenuItemClickListener);
    }

    private void importMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener(this::importBuys, this::importBills);
    }

    private void exportMenuItemClickListener() {
        MenuHelper.conditionalMenuItemClickListener(this::exportBuys, this::exportBills);
    }

    private void deleteAllMenuItemClickListener() {
        DialogUtils.confirmationDialog(context,
                R.string.delete_all_records_title,
                R.string.delete_all_records_message,
                () -> MenuHelper.conditionalMenuItemClickListener(this::deleteAllBuys, this::deleteAllBills));
    }

    private void restoreDefaultMenuItemClickListener() {
        DialogUtils.confirmationDialog(context,
                R.string.restore_default_records_title,
                R.string.restore_default_records_message,
                () -> MenuHelper.conditionalMenuItemClickListener(this::restoreDefaultBuys, this::restoreDefaultBills));
    }

    public static Context getContext() {
        return context;
    }
}

