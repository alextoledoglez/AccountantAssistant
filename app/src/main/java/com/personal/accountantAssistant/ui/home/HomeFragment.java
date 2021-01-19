package com.personal.accountantAssistant.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.db.LocalStorage;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.DatePickerDialogUtils;
import com.personal.accountantAssistant.utils.DateUtils;
import com.personal.accountantAssistant.utils.EditableTextsUtils;
import com.personal.accountantAssistant.utils.MenuHelper;
import com.personal.accountantAssistant.utils.NumberUtils;
import com.personal.accountantAssistant.utils.PaymentsFragmentsUtils;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        MenuHelper.initializeHomeOptions();
        final Context context = getContext();
        final Activity activity = getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_home, container, Boolean.FALSE);

        //Money
        final View availableMoneyCard = rootView.findViewById(R.id.available_card);
        final EditText availableMoneyEditText = availableMoneyCard.findViewById(R.id.card_edit_text);
        availableMoneyEditText.setText(String.valueOf(LocalStorage.getAvailableMoney()));
        EditableTextsUtils.initializeListeners(activity, availableMoneyEditText, () -> {
            LocalStorage.setAvailableMoney(Float.parseFloat(EditableTextsUtils.getEditTextValue()));
            calculateExpenses(context, activity, rootView);
        });

        //Period
        final TextView periodCardSubTitle = rootView.findViewById(R.id.period_card_text_view);
        periodCardSubTitle.setText(DateUtils.toPeriodStr(LocalStorage.getFirstDate(), LocalStorage.getLastDate()));

        final CalendarPickerView calendarPickerView = rootView.findViewById(R.id.period_calendar_picker_view);
        DatePickerDialogUtils.initializeCalendarPickerView(calendarPickerView,
                () -> periodCardSubTitle.setText(DateUtils.toPeriodStr(LocalStorage.getFirstDate(), LocalStorage.getLastDate())),
                () -> {
                    calculateExpenses(context, activity, rootView);
                    EditableTextsUtils.hideSoftInputFromWindow(activity, availableMoneyEditText);
                });

        calculateExpenses(context, activity, rootView);

        return rootView;
    }

    private void calculateExpenses(final Context context,
                                   final Activity activity,
                                   final View rootView) {

        final double availableMoney = LocalStorage.getAvailableMoney();
        fillDashBoardCard(rootView, R.id.available_card, String.valueOf(availableMoney));

        int periodDays = DateUtils.getDaysBetween(LocalStorage.getFirstDate(), LocalStorage.getLastDate());
        periodDays = periodDays > 0 ? periodDays : 1;
        final double dailyExpenses = NumberUtils.roundTo(availableMoney / periodDays);
        fillDashBoardCard(rootView, R.id.daily_card, String.valueOf(dailyExpenses));

        final Date lastPeriodDate = LocalStorage.getLastDate();

        final PaymentsFragmentsUtils buysUtils = new PaymentsFragmentsUtils(context, activity, PaymentsType.BUY);
        final Double buysExpenses = buysUtils.getTotalPriceUntil(lastPeriodDate);
        fillDashBoardCard(rootView, R.id.buy_card, String.valueOf(buysExpenses));

        final PaymentsFragmentsUtils billsUtils = new PaymentsFragmentsUtils(context, activity, PaymentsType.BILL);
        final Double billsExpenses = billsUtils.getTotalPriceUntil(lastPeriodDate);
        fillDashBoardCard(rootView, R.id.bill_card, String.valueOf(billsExpenses));

        final double totalExpenses = NumberUtils.roundTo(dailyExpenses + buysExpenses + billsExpenses);
        fillDashBoardCard(rootView, R.id.total_card, String.valueOf(totalExpenses));

        final double neededExpenses = NumberUtils.roundTo(availableMoney - totalExpenses);
        fillDashBoardCard(rootView, R.id.needed_card, String.valueOf(neededExpenses));
    }

    private boolean isMoreThanAvailableMoney(final String value) {
        return Double.parseDouble(value) > LocalStorage.getAvailableMoney();
    }

    private boolean isMoreThanOrEqualToZero(final String value) {
        return Double.parseDouble(value) >= 0;
    }

    private void setTitleTextViewStyleFor(final TextView cardTitleTextView,
                                          final int textSizeResource,
                                          final int fontColorResource,
                                          final int backgroundColorResource) {
        cardTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeResource);
        cardTitleTextView.setTextColor(fontColorResource);
        cardTitleTextView.setBackgroundColor(backgroundColorResource);
    }

    private int getTitleColorResourceFrom(final int idResource,
                                          final String cardTextValue) {
        final Context context = getContext();
        final int goodColorResource = Objects
                .requireNonNull(context)
                .getColor(R.color.colorPrimaryMedium);
        final int badColorResource = Objects
                .requireNonNull(context)
                .getColor(R.color.colorAccent);
        return getColorResourceFrom(idResource, cardTextValue, goodColorResource, badColorResource);
    }

    private int getColorResourceFrom(final int idResource,
                                     final String cardTextValue) {
        final Context context = getContext();
        final int goodColorResource = Objects
                .requireNonNull(context)
                .getColor(R.color.colorPrimary);
        final int badColorResource = Objects
                .requireNonNull(context)
                .getColor(R.color.colorAccent);
        return getColorResourceFrom(idResource, cardTextValue, goodColorResource, badColorResource);
    }

    private int getColorResourceFrom(final int idResource,
                                     final String cardTextValue,
                                     final int goodColorResource,
                                     final int badColorResource) {
        if (idResource == R.id.needed_card) {
            return isMoreThanOrEqualToZero(cardTextValue) ?
                    goodColorResource :
                    badColorResource;
        } else {
            return isMoreThanAvailableMoney(cardTextValue) ?
                    badColorResource :
                    goodColorResource;
        }
    }

    private void setCardViewStyle(final CardView cardView,
                                  final int cardVisibility,
                                  final int textSizeResource,
                                  final int fontColorResource,
                                  final int backgroundColorResource) {
        final ImageView imageView = cardView.findViewById(R.id.card_image);
        imageView.setVisibility(cardVisibility);
        final TextView cardTitleTextView = cardView.findViewById(R.id.card_title);
        setTitleTextViewStyleFor(cardTitleTextView, textSizeResource, fontColorResource, backgroundColorResource);
    }

    private void setTitleCardViewStyle(final int idResource,
                                       final CardView cardView,
                                       final String cardTextValue,
                                       final int cardTextSizeResource) {
        final Context context = getContext();
        final int cardVisibility = View.GONE;
        final int fontColorResource = Objects.requireNonNull(context).getColor(R.color.colorBlack);
        final int backgroundColorResource = getTitleColorResourceFrom(idResource, cardTextValue);
        setCardViewStyle(cardView, cardVisibility, cardTextSizeResource, fontColorResource, backgroundColorResource);
    }

    @SuppressLint("NonConstantResourceId")
    private void fillDashBoardCard(final View root,
                                   final int idResource,
                                   final String cardTextValue) {

        String cardTitle = "";
        int imageResource = 0;
        final Context context = getContext();

        int fontColorResource = getColorResourceFrom(idResource, cardTextValue);
        int backgroundColorResource = Objects.requireNonNull(context).getColor(R.color.colorWhite);

        final int textSizeResource = getResources().getDimensionPixelSize(R.dimen.card_title_text_size);
        final int titleTextSizeResource = getResources().getDimensionPixelSize(R.dimen.card_title_text_big_size);

        final CardView cardView = root.findViewById(idResource);
        final ImageView imageView = cardView.findViewById(R.id.card_image);
        imageView.setVisibility(View.VISIBLE);
        final TextView cardTitleTextView = cardView.findViewById(R.id.card_title);
        setCardViewStyle(cardView, View.VISIBLE, textSizeResource, fontColorResource, backgroundColorResource);

        switch (idResource) {

            case R.id.available_card:
                cardTitle = getString(R.string.available);
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource);
                break;
            case R.id.needed_card:
                if (isMoreThanOrEqualToZero(cardTextValue)) {
                    cardTitle = getString(R.string.gain);
                } else {
                    cardTitle = getString(R.string.missing);
                }
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource);
                break;
            case R.id.total_card:
                cardTitle = getString(R.string.total);
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource);
                break;
            case R.id.daily_card:
                cardTitle = getString(R.string.daily);
                imageResource = isMoreThanAvailableMoney(cardTextValue) ?
                        R.drawable.ic_menu_red_daily :
                        R.drawable.ic_menu_green_daily;
                break;
            case R.id.buy_card:
                cardTitle = getString(R.string.menu_buys);
                imageResource = isMoreThanAvailableMoney(cardTextValue) ?
                        R.drawable.ic_menu_red_buys :
                        R.drawable.ic_menu_green_buys;
                break;
            case R.id.bill_card:
                cardTitle = getString(R.string.menu_bills);
                imageResource = isMoreThanAvailableMoney(cardTextValue) ?
                        R.drawable.ic_menu_red_bills :
                        R.drawable.ic_menu_green_bills;
                break;
        }
        cardTitleTextView.setText(cardTitle);
        imageView.setImageResource(imageResource);

        if (idResource != R.id.available_card) {
            final TextView cardSubTitleTextView = cardView.findViewById(R.id.card_sub_title);
            cardSubTitleTextView.setText(String.valueOf(Math.abs(Double.parseDouble(cardTextValue))));
            cardSubTitleTextView.setTextColor(fontColorResource);
        }
    }
}