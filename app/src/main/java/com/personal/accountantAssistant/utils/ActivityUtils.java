package com.personal.accountantAssistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.personal.accountantAssistant.ui.payments.PaymentsDetailsActivity;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;

import java.util.Objects;

public class ActivityUtils {

    public static void startActivity(final Context packageContext,
                                     final Class<?> activityClass) {
        final Intent intent = new Intent(packageContext, activityClass);
        packageContext.startActivity(intent);
    }

    public static void startPaymentDetailsActivity(final Context packageContext,
                                                   final PaymentsType paymentsType) {
        final Activity activity = parse(packageContext);
        final Payments payment = new Payments();
        payment.setType(paymentsType);
        final Intent activityIntent = new Intent(packageContext, PaymentsDetailsActivity.class);
        activityIntent.putExtra(Constants.ENTITY, payment);
        activity.startActivityForResult(activityIntent, Constants.DETAIL_REQUEST_CODE);
    }

    public static void refreshBy(final Context context) {
        final Activity activity = parse(context);
        Objects.requireNonNull(activity).recreate();
    }

    public static Activity parse(final Context context) {
        return (Activity) context;
    }

    public static Context parse(final Activity activity) {
        return activity.getApplicationContext();
    }
}
