package com.personal.accountantAssistant.utils;

import com.personal.accountantAssistant.ui.bills.entities.Bills;
import com.personal.accountantAssistant.ui.buys.entities.Buys;
import com.personal.accountantAssistant.ui.payments.entities.Payments;

import java.util.List;

public class ParserUtils {

    public static boolean isNullObject(final Object object) {
        return (object == null);
    }

    public static boolean isNotNullObject(final Object object) {
        return !isNullObject(object);
    }

    public static boolean isNotNullAndNotEmpty(final List<?> list) {
        return list != null &&
                !list.isEmpty();
    }

    public static boolean isNotNullAndNotEmpty(final String stringValue) {
        return stringValue != null &&
                !stringValue.isEmpty();
    }

    private static boolean isNullOrEmpty(final List<?> list) {
        return list == null ||
                list.isEmpty();
    }

    private static boolean isBuyInstance(final Object entity) {
        return entity instanceof Buys;
    }

    private static boolean isBillInstance(final Object entity) {
        return entity instanceof Bills;
    }

    public static Bills toBills(final Object entity) {
        if (isBillInstance(entity)) {
            return (Bills) entity;
        } else {
            return null;
        }
    }

    public static Buys toBuys(final Object entity) {
        if (isBuyInstance(entity)) {
            return (Buys) entity;
        } else {
            return null;
        }
    }

    public static Payments toPayments(final Object entity) {
        Payments payments = null;
        if (isNotNullObject(entity)) {
            if (ParserUtils.isBuyInstance(entity)) {
                payments = new Payments((Buys) entity);
            } else if (ParserUtils.isBillInstance(entity)) {
                payments = new Payments((Bills) entity);
            } else if (ParserUtils.isPaymentInstance(entity)) {
                payments = (Payments) entity;
            }
        }
        return payments;
    }

    private static boolean isPaymentInstance(Object entity) {
        return entity instanceof Payments;
    }
}
