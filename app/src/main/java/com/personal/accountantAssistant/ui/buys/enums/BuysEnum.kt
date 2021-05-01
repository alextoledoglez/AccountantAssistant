package com.personal.accountantAssistant.ui.buys.enums;

public enum BuysEnum {

    RICE("ARROZ"),
    BEAN("FEIJÃO"),
    SUGAR("AÇÚCAR"),
    FLOUR("FARINHA"),
    EGGS("OVOS"),
    COFFEE("CAFÉ"),
    MILK("LEITE"),
    SALT("SAL"),
    OIL("ÓLEO"),
    TOOTH_PASTE("CREME DENTAL"),
    TOILET_PAPER("PAPEL HIGIÊNICO"),
    SOAP("SABONETE"),
    BAR_SOAP("SABÃO EM BARRAS"),
    WASHING_POWDER("SABÃO EM PÓ"),
    LIQUID_DETERGENT("DETERGENTE LÍQUIDO"),
    BUTTER("MANTEIGA"),
    ONION("CEBOLA"),
    GARLIC("ALHO"),
    CUMIN("CUMINHO"),
    LEMON("LIMAO"),
    SOFTENER("AMACIANTE"),
    SANITARY_WATER("AGUA SANITARIA"),
    APPLE("MAÇA"),
    JUICE("SUCO"),
    BEER("CERVEJA"),
    ICE_CREAM("SORVETE"),
    SODA("REFRIGERANTE");

    String value;

    BuysEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
