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

                    return new ModelAndView(m, "login.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    if (username == null) {
                        throw new Exception("Login name not found");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        user = new User(username, "");
                        users.put(username, user);
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect(request.headers("Referer"));
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
    }
}
