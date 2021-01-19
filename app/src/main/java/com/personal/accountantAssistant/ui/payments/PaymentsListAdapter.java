package com.personal.accountantAssistant.ui.payments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.db.DatabaseManager;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.ActivityUtils;
import com.personal.accountantAssistant.utils.CalculatorUtils;
import com.personal.accountantAssistant.utils.Constants;
import com.personal.accountantAssistant.utils.DataBaseUtils;
import com.personal.accountantAssistant.utils.DateUtils;
import com.personal.accountantAssistant.utils.MenuHelper;
import com.personal.accountantAssistant.utils.NumberUtils;
import com.personal.accountantAssistant.utils.ParserUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static java.util.stream.Collectors.toList;

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.ViewHolderData> implements Filterable {

    private final Context context;
    private List<Payments> payments;
    private final PaymentsType paymentsType;
    private final DatabaseManager databaseManager;

    public PaymentsListAdapter(final Context context,
                               final PaymentsType paymentsType) {
        this.context = context;
        this.paymentsType = paymentsType;
        this.databaseManager = new DatabaseManager(context);
        initializePayments();
    }

    public void initializePayments() {
        payments = databaseManager
                .getPaymentsRecords()
                .stream()
                .filter(it -> it.getType().equals(paymentsType))
                .sorted(Comparator.comparing(Payments::getDate))
                .collect(toList());
    }

    public void setAllPaymentsRecordsActiveFrom(final boolean isActive) {
        payments.forEach(payment -> setActiveRowFrom(isActive, payment));
    }

    private int getLayoutToInflate() {
        return PaymentsType.isBuy(paymentsType) ?
                R.layout.buys_item_list :
                R.layout.bills_item_list;
    }

    @NonNull
    @Override
    public PaymentsListAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(getLayoutToInflate(), null, Boolean.FALSE);
        return new ViewHolderData(view, paymentsType);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsListAdapter.ViewHolderData viewHolderData,
                                 int position) {

        final Payments payment = payments.get(position);

        if (!ParserUtils.isNullObject(payment)) {

            if (PaymentsType.isBuy(paymentsType) && payment.isBuy()) {
                //INITIALIZE
                MenuHelper.initializeBuysOptions();
            }

            if (PaymentsType.isBill(paymentsType) && payment.isBill()) {
                //INITIALIZE
                MenuHelper.initializeBillsOptions();
                viewHolderData.date.setText(DateUtils.toString(payment.getDate()));
            }

            //DETAILS
            viewHolderData.name.setText(payment.getName());
            viewHolderData.value.setText(toFormattedValue(payment));

            //ACTIONS
            viewHolderData.active.setChecked(payment.isActive());
            viewHolderData.active.setOnClickListener(view -> {
                final boolean isChecked = viewHolderData.active.isChecked();
                setActiveRowFrom(isChecked, payment);
            });
            viewHolderData.itemView.setOnClickListener(view -> editRecordFrom(payment));
            viewHolderData.deleteItem.setOnClickListener(view -> deleteRecordFrom(payment));
            setRowForegroundFrom(viewHolderData);
        }
    }

    private void setActiveRowFrom(final boolean isActive, final Payments payment) {
        payment.setActive(isActive);
        final long updateRecord = databaseManager.updatePaymentsRecordFrom(payment);
        if (DataBaseUtils.isNotDefault(updateRecord)) {
            notifyDataSetChanged();
        }
    }

    private void editRecordFrom(final Payments payment) {
        final Activity activity = ActivityUtils.parse(context);
        final Intent activityIntent = new Intent(context, PaymentsDetailsActivity.class);
        activityIntent.putExtra(Constants.ENTITY, payment);
        activity.startActivityForResult(activityIntent, Constants.DETAIL_REQUEST_CODE);
    }

    private void deleteRecordFrom(final Payments payment) {
        DataBaseUtils.deleteRecord(context, payment, () -> {
            payments.remove(payment);
            notifyDataSetChanged();
        });
    }

    private void setRowForegroundFrom(@NonNull PaymentsListAdapter.ViewHolderData viewHolderData) {
        final int color = viewHolderData.active.isChecked() ?
                context.getColor(R.color.defaultFontColor) :
                context.getColor(R.color.disableForegroundColor);
        viewHolderData.name.setTextColor(color);
        if (PaymentsType.isBill(paymentsType)) {
            viewHolderData.date.setTextColor(color);
        }
        viewHolderData.value.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final String filterStr = charSequence.toString();
                if (filterStr.isEmpty()) {
                    initializePayments();
                } else {
                    payments = payments
                            .stream()
                            .filter(it -> it.getName().toLowerCase().contains(filterStr.toLowerCase()))
                            .collect(toList());
                }
                final FilterResults filterResults = new FilterResults();
                filterResults.values = payments;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                payments = (List<Payments>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolderData extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;
        TextView value;

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch active;
        ImageButton deleteItem;

        ViewHolderData(@NonNull View itemView,
                       final PaymentsType paymentsType) {

            super(itemView);

            //DETAILS
            name = itemView.findViewById(R.id.name);
            if (PaymentsType.isBill(paymentsType)) {
                date = itemView.findViewById(R.id.date);
            }
            value = itemView.findViewById(R.id.value);

            //ACTIONS
            active = itemView.findViewById(R.id.active_action);
            deleteItem = itemView.findViewById(R.id.delete_action);
        }
    }

    public Double getTotalPrice() {
        final double totalPrice = payments
                .stream()
                .filter(Payments::isActive)
                .map(Payments::getTotalValue)
                .reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum);
        return NumberUtils.roundTo(totalPrice);
    }

    public Double getTotalPriceUntil(final Date lastPeriodDate) {
        final Double totalPrice = payments
                .stream()
                .filter(it -> it.isActive() && DateUtils.isInRange(it.getDate(), lastPeriodDate))
                .map(Payments::getTotalValue)
                .reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum);
        return NumberUtils.roundTo(totalPrice);
    }

    private String toFormattedValue(final Payments payment) {
        String quantityStr = String.valueOf(payment.getQuantity());
        quantityStr += payment.isBuy() ?
                Constants.UNITY :
                Constants.TIMES;
        return quantityStr +
                Constants.MULTIPLY_OPERATOR +
                payment.getUnitaryValue() +
                Constants.EQUAL_OPERATOR +
                NumberUtils.roundTo(payment.getTotalValue());
    }

    public void notifyItemAddedOrChanged(final Payments payment) {

        int paymentId = payment.getId();

        if (paymentId <= 0) {
            initializePayments();
        }

        final Payments updatedPayment = payments.stream()
                .filter(it -> it.equalsTo(payment))
                .findFirst().orElse(payment);
        paymentId = updatedPayment.getId();

        if (paymentId <= 0) {
            payments.add(payment);
            int position = (getItemCount() - 1);
            notifyItemInserted(position);
        } else {
            int paymentsSize = getItemCount();
            for (int position = 0; position < paymentsSize; position++) {
                final Payments item = payments.get(position);
                if (paymentId == item.getId()) {
                    payments.set(position, payment);
                    notifyItemChanged(position, payment);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }
}

