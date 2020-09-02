package com.personal.accountantAssistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.personal.accountantAssistant.ui.payments.PaymentsDetailsActivity;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;

import java.util.Objects;

public class ActivityUtils {

    public static void startActivity(final Context packageContext,
                                     final Class<?> activityClass) {
        final Intent intent = new Intent(packageContext, activityClass);
        packageContext.startActivity(intent);
    }

    public static void startActivity(final Context packageContext,
                                     final Class<?> activityClass,
                                     final PaymentsType paymentsType) {
        final Intent intent = new Intent(packageContext, activityClass);
        if (activityClass.equals(PaymentsDetailsActivity.class)) {
            putExtraIntent(intent, paymentsType);
        }
        packageContext.startActivity(intent);
    }

    public static void startDetailsActivity(final Context packageContext,
                                            final Object entity) {
        Intent activityIntent = null;
        final Payments payment = ParserUtils.toPayments(entity);

        if (!ParserUtils.isNullObject(payment)) {
            activityIntent = new Intent(packageContext, PaymentsDetailsActivity.class);
        }

        if (!ParserUtils.isNullObject(activityIntent)) {
            putExtraIntent(activityIntent, payment);
            packageContext.startActivity(activityIntent);
        }
    }

    private static void putExtraIntent(final Intent intent,
                                       final Payments payment) {
        intent.putExtra(Constants.UID, payment.getId());
        intent.putExtra(PaymentsEnum.NAME.name(), payment.getName());
        intent.putExtra(PaymentsEnum.QUANTITY.name(), payment.getQuantity());
        intent.putExtra(PaymentsEnum.DATE.name(), DateUtils.toString(payment.getDate()));
        intent.putExtra(PaymentsEnum.UNITARY_VALUE.name(), payment.getUnitaryValue());
        intent.putExtra(PaymentsEnum.TOTAL_VALUE.name(), payment.getTotalValue());
        intent.putExtra(PaymentsEnum.TYPE.name(), payment.getType().name());
        intent.putExtra(PaymentsEnum.ACTIVE.name(), payment.isActive());
    }

    private static void putExtraIntent(final Intent intent,
                                       final PaymentsType paymentsType) {
        intent.putExtra(Constants.UID, Constants.DEFAULT_UID);
        intent.putExtra(PaymentsEnum.NAME.name(), Constants.EMPTY_STR);
        intent.putExtra(PaymentsEnum.QUANTITY.name(), Constants.DEFAULT_QUANTITY_VALUE);
        intent.putExtra(PaymentsEnum.DATE.name(), DateUtils.toString(Constants.DEFAULT_DATE_VALUE));
        intent.putExtra(PaymentsEnum.UNITARY_VALUE.name(), Constants.DEFAULT_VALUE);
        intent.putExtra(PaymentsEnum.TOTAL_VALUE.name(), Constants.DEFAULT_VALUE);
        intent.putExtra(PaymentsEnum.TYPE.name(), paymentsType.name());
        intent.putExtra(PaymentsEnum.ACTIVE.name(), Constants.DEFAULT_ACTIVE_STATUS);
    }

    public static void refreshBy(final Context context) {
        final Activity activity = parse(context);
        Objects.requireNonNull(activity).recreate();
    }

    public static Activity parse(final Context context) {
        return (Activity) context;
    }
}
