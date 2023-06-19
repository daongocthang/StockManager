package com.standalone.mystocks2.api;

import android.content.SharedPreferences;

import com.standalone.mystocks2.constant.StringValues;
import com.standalone.mystocks2.models.Stock;

public class SettingsUtil {
    public static double estimateTransactionCost(SharedPreferences preferences, Stock.OrderType type) {
        double rate = 0;
        double transactionFee = (double) preferences.getInt(StringValues.TRANSACTION_COST, 0);
        switch (type) {
            case BUY:
                rate = 1 + transactionFee / 10000;
                break;
            case SELL:
                double valueAddedTax = (double) preferences.getInt(StringValues.VALUE_ADDED_TAX, 0);
                rate = 1 - ((transactionFee + valueAddedTax) / 10000);
        }
        return rate;
    }

    public static double getStopLossThreshold(SharedPreferences preferences) {
        return (double) preferences.getInt(StringValues.STOP_THRESHOLD, 0) / 100;
    }
}
