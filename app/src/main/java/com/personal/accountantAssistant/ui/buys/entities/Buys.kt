package com.personal.accountantAssistant.ui.buys.entities;

import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.utils.Constants;

public class Buys {

    private int id;
    private String product;
    private int quantity;
    private double price;
    private double totalValue;
    private boolean active;

    public Buys() {
    }

    public Buys(String product) {
        this.product = product;
        this.quantity = Constants.DEFAULT_QUANTITY_VALUE;
        this.price = Constants.DEFAULT_VALUE;
        this.totalValue = Constants.DEFAULT_VALUE;
        this.active = Constants.DEFAULT_ACTIVE_STATUS;
    }

    public Buys(final int id,
                final String product,
                final int quantity,
                final double price,
                final boolean active) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.active = active;
    }

    public Buys(final Payments payments) {
        this.id = payments.getId();
        this.product = payments.getName();
        this.quantity = payments.getQuantity();
        this.price = payments.getUnitaryValue();
        this.totalValue = payments.getTotalValue();
        this.active = payments.isActive();
    }

    public void setUid(int uid) {
        this.id = uid;
    }

    public int getUid() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
