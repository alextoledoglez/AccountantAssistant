package com.personal.accountantAssistant.utils;

import java.util.Date;

public class Constants {

    public final static String UID = "ID";
    public final static String TIMES = "x";
    public final static String UNITY = "u";
    public final static String EMPTY_STR = "";
    public final static String ENTITY = "ENTITY";
    public final static String DASH_SEPARATOR = "-";
    public final static String EQUAL_OPERATOR = " = ";
    public final static String BAR_CODE_KEY = "barcode";
    public final static String MULTIPLY_OPERATOR = " * ";
    private final static String STR_DEFAULT_QUANTITY_VALUE = "00";
    public final static String STR_DEFAULT_MONETARY_VALUE = "00.00";
    private final static String STR_DEFAULT_VALUE = Constants.STR_DEFAULT_MONETARY_VALUE;

    public final static int DEFAULT_UID = 0;
    public final static int DETAIL_REQUEST_CODE = 123;
    public final static Date DEFAULT_DATE_VALUE = new Date();
    public final static boolean DEFAULT_ACTIVE_STATUS = Boolean.FALSE;
    public final static double DEFAULT_VALUE = Double.parseDouble(STR_DEFAULT_VALUE);
    public final static int DEFAULT_QUANTITY_VALUE = Integer.parseInt(STR_DEFAULT_QUANTITY_VALUE);
}
