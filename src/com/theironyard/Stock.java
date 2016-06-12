package com.theironyard;

/**
 * Created by Ben on 6/9/16.
 */
public class Stock {
    String name;
    String symbol;
    double price;
    double shares;
    double dividend;
    double value;

    public Stock(String name, String symbol, double price, double shares, double dividend, double value) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;
        this.dividend = dividend;
        this.value= value;

    }
}
