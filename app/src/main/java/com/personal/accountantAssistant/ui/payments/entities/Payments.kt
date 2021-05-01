package com.personal.accountantAssistant.ui.payments.entities;

import com.personal.accountantAssistant.ui.bills.entities.Bills;
import com.personal.accountantAssistant.ui.buys.entities.Buys;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;

import java.io.Serializable;
import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Payments implements Serializable {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "unitary_value")
    private double unitaryValue;

    @ColumnInfo(name = "total_value")
    private double totalValue;

    @ColumnInfo(name = "type")
    private PaymentsType type;

    @ColumnInfo(name = "active")
    private boolean active;

    public Payments() {
    }

    public Payments(final int id,
                    final String name,
                    final int quantity,
                    final Date date,
                    final double unitaryValue,
                    final PaymentsType type,
                    final boolean active) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.date = date;
        this.unitaryValue = unitaryValue;
        this.totalValue = getTotalValue();
        this.type = type;
        this.active = active;
    }

    public Payments(final Bills bill) {
        this.id = bill.getUid();
        this.name = bill.getBill();
        this.quantity = bill.getQuantity();
        this.date = bill.getDate();
        this.unitaryValue = bill.getValue();
        this.totalValue = getTotalValue();
        this.type = PaymentsType.BILL;
        this.active = bill.isActive();
    }

    public Payments(final Buys buy) {
        this.id = buy.getUid();
        this.name = buy.getProduct();
        this.quantity = buy.getQuantity();
        this.date = new Date();
        this.unitaryValue = buy.getPrice();
        this.totalValue = getTotalValue();
        this.type = PaymentsType.BUY;
        this.active = buy.isActive();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getUnitaryValue() {
        return unitaryValue;
    }

    public void setUnitaryValue(double unitaryValue) {
        this.unitaryValue = unitaryValue;
    }

    public double getTotalValue() {
        totalValue = (unitaryValue * quantity);
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PaymentsType getType() {
        return type;
    }

    public void setType(PaymentsType type) {
        this.type = type;
    }

    public boolean isBill() {
        return PaymentsType.isBill(this.getType());
    }

    public boolean isBuy() {
        return PaymentsType.isBuy(this.getType());
    }

    public boolean equalsTo(final Payments payment) {
        return this.getType().equals(payment.getType()) &&
                this.getName().equals(payment.getName()) &&
                this.getQuantity() == payment.getQuantity() &&
                this.getUnitaryValue() == payment.getUnitaryValue();
    }
}
