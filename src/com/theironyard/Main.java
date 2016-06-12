package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Stock> stocks = new ArrayList<>();

    public static void main(String[] args) {





        Spark.init();
        Spark.get (
                "/",
                (request, response) -> {

                    Session session = request.session();
                    String username = session.attribute("username");



                    HashMap m = new HashMap<>();

                    m.put("username", username );
                    if (username == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        User user = users.get(username);
                        m.put("stocks", user.stocks);
                        return new ModelAndView(m, "stocks.html");
                    }

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String pass = request.queryParams("password");
                    if (name == null || pass == null) {
                        throw new Exception("Name or pass not sent");
                    }

                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, pass);
                        users.put(name, user);
                    } else if (!pass.equals(user.password)) {
                        throw new Exception("Wrong password");
                    }

                    Session session = request.session();
                    session.attribute("username", name);

                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();



                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/create-stock",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    String name = request.queryParams("name");
                    String symbol = request.queryParams("symbol");
                    String priceStr = request.queryParams("price");
                    String sharesStr = request.queryParams("shares");
                    String dividendStr = request.queryParams("dividend");

                    if (name == null ||  symbol == null || priceStr == null || sharesStr == null ||  dividendStr == null) {
                        throw new Exception("Invalid form fields");
                    }

                    double price = Double.valueOf(priceStr);
                    double shares = Double.valueOf(sharesStr);
                    double dividend = Double.valueOf(dividendStr);
                    double value = price * shares;


                    User user = users.get(username);

                    if (user == null) {
                        throw new Exception("User does not exist");
                    }



                    Stock s  =  new Stock(name, symbol, price, shares, dividend, value);


                    user.stocks.add(s);

                    response.redirect("/");
                    return "";
                }
        );
    }
}
