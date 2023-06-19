package com.standalone.mystocks2.models;

public class Company {
    private final static String LONG_PHRASES = "(?i)công (?i)ty (?i)cổ (?i)phần";
    private final static String SHORT_PHRASES = "CTCP";
    private String stockNo;
    private String symbol;
    private String shortName;

    public Company() {
    }

    public Company(String stockNo, String symbol, String shortName) {
        this.stockNo = stockNo;
        this.symbol = symbol;
        this.shortName = shortName;
    }

    public String getStockNo() {
        return stockNo;
    }

    public void setStockNo(String stockNo) {
        this.stockNo = stockNo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName.replaceAll(LONG_PHRASES, SHORT_PHRASES);
    }
}
