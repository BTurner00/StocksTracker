package com.theironyard;

import java.util.ArrayList;

/**
 * Created by Ben on 6/9/16.
 */
public class User {
    String username;
    String password;
    int id;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    ArrayList<Stock> stocks = new ArrayList<>();

    public User(String username, String password, ArrayList<Stock> stocks) {
        this.username = username;
        this.password = password;
        this.stocks = stocks;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
