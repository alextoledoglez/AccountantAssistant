package com.personal.accountantAssistant.ui.bills.enums;

public enum BillsEnum {

    WATER("ÁGUA"),
    ENERGY("ENERGIA"),
    INTERNET("INTERNET"),
    PHONE("TELEFONE"),
    MOBILE("MÓVEL"),
    CREDIT_CARD("CARTÃO DE CRÉDITO");

    String value;

    BillsEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}