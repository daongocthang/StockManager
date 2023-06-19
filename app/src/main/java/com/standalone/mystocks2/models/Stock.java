package com.standalone.mystocks2.models;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.standalone.mystocks2.api.SettingsUtil;

import java.io.Serializable;
import java.util.Comparator;

public class Stock implements Serializable, Comparable<Stock> {

    @Override
    public int compareTo(Stock other) {
        try {
            return other.getDate().compareTo(this.getDate());
        } catch (RuntimeException e) {
            return this.getSymbol().compareTo(other.getSymbol());
        }
    }

    public enum OrderType {
        BUY, SELL
    }

    int id;
    String symbol;
    double price;
    int shares;
    double reference;
    OrderType order;
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public double getReference() {
        return reference;
    }

    public void setReference(double reference) {
        this.reference = reference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrderType getOrder() {
        return order;
    }

    public void setOrder(OrderType order) {
        this.order = order;
    }

    public double getNetProfit(SharedPreferences settings) {
        double refPrice = getReference() * SettingsUtil.estimateTransactionCost(settings, OrderType.BUY);
        double matchedPrice = getPrice() * SettingsUtil.estimateTransactionCost(settings, OrderType.SELL);
        return (matchedPrice - refPrice) * getShares();
    }

}
