package com.personal.accountantAssistant.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.ui.payments.PaymentsListAdapter;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentsFragmentsUtils {

    private final Context context;
    private final Activity activity;
    private final PaymentsType paymentsType;

    private ImageView titleImageView;
    private TextView subTitleTextView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch checker;
    private RecyclerView recyclerView;
    private PaymentsListAdapter adapter;

    public PaymentsFragmentsUtils(final Context context,
                                  final Activity activity,
                                  final PaymentsType paymentsType) {
        this.context = context;
        this.activity = activity;
        this.paymentsType = paymentsType;
    }

    public Context getContext() {
        return context;
    }

    public void initializeVisualComponentsFrom(final View viewRoot) {
        final View headerCardTitlesBar = viewRoot.findViewById(R.id.header_card_titles_bar);

        //Title
        final TextView headerCardTitle = headerCardTitlesBar.findViewById(R.id.titles_bar_title);
        headerCardTitle.setVisibility(View.GONE);

        //Image
        titleImageView = headerCardTitlesBar.findViewById(R.id.title_image);

        //Subtitle
        subTitleTextView = headerCardTitlesBar.findViewById(R.id.titles_bar_subtitle);
        subTitleTextView.setText(String.valueOf(Constants.DEFAULT_VALUE));

        //Switch
        checker = headerCardTitlesBar.findViewById(R.id.title_switch);
        checker.setOnClickListener(view -> adapter.setAllPaymentsRecordsActiveFrom(checker.isChecked()));

        //Search View
        final SearchView searchView = headerCardTitlesBar.findViewById(R.id.title_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryStr) {
                recyclerViewAdapterFilterBy(queryStr);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapterFilterBy(newText);
                return false;
            }
        });

        adapter = new PaymentsListAdapter(context, paymentsType);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateRecyclerView();
            }
        });

        //Recycler View
        if (PaymentsType.isBuy(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.buy_list);
        }

        if (PaymentsType.isBill(paymentsType)) {
            recyclerView = viewRoot.findViewById(R.id.bills_list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    public void updateRecyclerView() {

        if (PaymentsType.isBuy(paymentsType)) {
            MenuHelper.initializeBuysOptions();
        }

        if (PaymentsType.isBill(paymentsType)) {
            MenuHelper.initializeBillsOptions();
        }

        final boolean anyActive = DataBaseUtils.anyActivePaymentsRecordsBy(activity, paymentsType);
        titleImageView.setImageResource(anyActive ?
                R.drawable.ic_red_money :
                R.drawable.ic_menu_green_money);
        subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        subTitleTextView.setTextColor(anyActive ?
                context.getColor(R.color.colorAccent) :
                context.getColor(R.color.colorPrimary));

        subTitleTextView.setText(String.valueOf(adapter.getTotalPrice()));
        checker.setChecked(DataBaseUtils.allActivePaymentsRecordsBy(activity, paymentsType));
    }

    private void recyclerViewAdapterFilterBy(final String queryStr) {
        if (ParserUtils.isNotNullObject(adapter)) {
            final Filter paymentsFilter = adapter.getFilter();
            if (ParserUtils.isNotNullObject(paymentsFilter)) {
                paymentsFilter.filter(queryStr);
            }
        }
    }

    public void onDetailsActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.DETAIL_REQUEST_CODE) {
            if (ParserUtils.isNotNullObject(resultData)) {
                final Object entity = resultData.getSerializableExtra(Constants.ENTITY);
                final Payments payment = ParserUtils.toPayments(entity);
                if (ParserUtils.isNotNullObject(payment)) {
                    adapter.notifyItemAddedOrChanged(payment);
                }
            }
        }
    }
}
