package com.theironyard;

/**
 * Created by Ben on 6/9/16.
 */
public class Stock {
    String name;
    String symbol;
    double price;
    double shares;
    int id;



    public Stock(int id, String name, String symbol, double price, double shares) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;
    }

    public Stock(String name, String symbol, double price, double shares) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;

    }
}
