package com.personal.accountantAssistant.ui.payments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.ViewHolderData> {

    private Context context;
    private Double totalPrice;
    private DatabaseManager databaseManager;
    private PaymentsType paymentsType;

    public PaymentsListAdapter(final Context context,
                               final PaymentsType paymentsType) {
        this.context = context;
        this.paymentsType = paymentsType;
        this.databaseManager = new DatabaseManager(context);
        this.totalPrice = getFilteredPaymentsRecords()
                .stream()
                .filter(Payments::isActive)
                .map(Payments::getTotalValue)
                .reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum);
    }

    public List<Payments> getFilteredPaymentsRecords() {
        return databaseManager
                .getPaymentsRecords()
                .stream()
                .filter(it -> it.getType().equals(paymentsType))
                .collect(Collectors.toList());
    }

    public void setAllPaymentsRecordsActiveFrom(final boolean isActive) {
        getFilteredPaymentsRecords()
                .forEach(payments -> {
                    payments.setActive(isActive);
                    final Activity activity = ActivityUtils.parse(context);
                    DataBaseUtils.saveDataFromWithoutFinish(activity, payments);
                    notifyDataSetChanged();
                });
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
        return new PaymentsListAdapter.ViewHolderData(view, paymentsType);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsListAdapter.ViewHolderData holder,
                                 int position) {

        final Payments payment = getFilteredPaymentsRecords().get(position);

        if (!ParserUtils.isNullObject(payment)) {

            if (PaymentsType.isBuy(paymentsType) && payment.isBuy()) {
                //INITIALIZE
                MenuHelper.initializeBuysOptions();
            }

            if (PaymentsType.isBill(paymentsType) && payment.isBill()) {
                //INITIALIZE
                MenuHelper.initializeBillsOptions();
                holder.date.setText(DateUtils.toString(payment.getDate()));
            }

            //DETAILS
            holder.name.setText(payment.getName());
            holder.value.setText(toFormattedValue(payment));

            //ACTIONS
            holder.active.setChecked(payment.isActive());
            holder.active.setOnClickListener(view -> setActiveRowFrom(holder, payment));
            holder.deleteItem.setOnClickListener(view -> DataBaseUtils.deleteRecord(context, payment));
            holder.itemView.setOnClickListener(view -> ActivityUtils.startDetailsActivity(context, payment));
            setRowForegroundFrom(holder);
        }
    }

    private void setActiveRowFrom(@NonNull PaymentsListAdapter.ViewHolderData holder,
                                  final Payments payment) {
        payment.setActive(holder.active.isChecked());
        if (DataBaseUtils.isNotDefault(databaseManager.updatePaymentsRecordFrom(payment))) {
            ActivityUtils.refreshBy(context);
        }
    }

    private void setRowForegroundFrom(@NonNull PaymentsListAdapter.ViewHolderData holder) {
        final int color = holder.active.isChecked() ?
                context.getColor(R.color.defaultFontColor) :
                context.getColor(R.color.disableForegroundColor);
        holder.name.setTextColor(color);
        if (PaymentsType.isBill(paymentsType)) {
            holder.date.setTextColor(color);
        }
        holder.value.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return getFilteredPaymentsRecords().size();
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
        return NumberUtils.roundTo(totalPrice);
    }

    public Double getTotalPriceUntil(final Date lastPeriodDate) {
        final Double total = getFilteredPaymentsRecords()
                .stream()
                .filter(it -> it.isActive() && DateUtils.isInRange(it.getDate(), lastPeriodDate))
                .map(Payments::getTotalValue)
                .reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum);
        return NumberUtils.roundTo(total);
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
}

