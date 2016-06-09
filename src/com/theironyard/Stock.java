package com.theironyard;

/**
 * Created by Ben on 6/9/16.
 */
public class Stock {
    String name;
    String symbol;
    int price;
    double shares;
    double dividend;

    public Stock(String name, String symbol, int price, double shares, double dividend) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;
        this.dividend = dividend;
    }
}
