package com.personal.accountantAssistant.ui.bills.entities;

import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.utils.Constants;

import java.util.Date;

public class Bills {

    private int id;
    private String bill;
    private int quantity;
    private Date date;
    private double value;
    private double totalValue;
    private boolean active;

    public Bills() {
    }

    public Bills(String bill) {
        this.bill = bill;
        this.quantity = Constants.DEFAULT_QUANTITY_VALUE;
        this.date = Constants.DEFAULT_DATE_VALUE;
        this.value = Constants.DEFAULT_VALUE;
        this.totalValue = Constants.DEFAULT_VALUE;
        this.active = Constants.DEFAULT_ACTIVE_STATUS;
    }

    public Bills(int id,
                 String bill,
                 int quantity,
                 Date date,
                 double value,
                 boolean active) {
        this.id = id;
        this.bill = bill;
        this.quantity = quantity;
        this.date = date;
        this.value = value;
        this.active = active;
    }

    public Bills(final Payments payments) {
        this.id = payments.getId();
        this.bill = payments.getName();
        this.quantity = payments.getQuantity();
        this.date = payments.getDate();
        this.value = payments.getUnitaryValue();
        this.totalValue = payments.getTotalValue();
        this.active = payments.isActive();
    }

    public void setUid(int uid) {
        this.id = uid;
    }

    public int getUid() {
        return id;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
