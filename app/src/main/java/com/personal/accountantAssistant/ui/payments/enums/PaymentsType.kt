package com.personal.accountantAssistant.ui.payments.enums;

import com.personal.accountantAssistant.utils.ParserUtils;

public enum PaymentsType {
    BUY,
    BILL;

    public static boolean isBuy(final String name) {
        return !ParserUtils.isNullObject(name) &&
                BUY.name().equals(name);
    }

    public static boolean isBuy(final PaymentsType paymentsType) {
        return !ParserUtils.isNullObject(paymentsType) &&
                isBuy(paymentsType.name());
    }

    public static boolean isBill(final String name) {
        return !ParserUtils.isNullObject(name) &&
                BILL.name().equals(name);
    }

    public static boolean isBill(final PaymentsType paymentsType) {
        return !ParserUtils.isNullObject(paymentsType) &&
                isBill(paymentsType.name());
    }
}
