package com.personal.accountantAssistant.ui.payments.enums;

public enum PaymentsEnum {

    NAME("NAME"),
    QUANTITY("QUANTITY"),
    DATE("DATE"),
    UNITARY_VALUE("UNITARY VALUE"),
    TOTAL_VALUE("TOTAL VALUE"),
    TYPE("TYPE"),
    ACTIVE("ACTIVE");

    String value;

    PaymentsEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
