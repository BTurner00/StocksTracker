package com.theironyard;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables (Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS stocks (id IDENTITY, name VARCHAR, symbol VARCHAR, price DOUBLE, shares DOUBLE, user_id INT)");
    }

    static void insertUser (Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    static User selectUser (Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password  = results.getString("password");
            return new User(id, name, password);
        }

        return null;
    }

    static void insertStock (Connection conn, String name, String symbol, double price, double shares, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO stocks VALUES (NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, symbol);
        stmt.setDouble(3, price);
        stmt.setDouble(4, shares);
        stmt.setInt(5, userId);

        stmt.execute();
    }

    static Stock selectStock (Connection conn, int id ) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stocks INNER JOIN users ON stocks.user_id = users.id WHERE users.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String name = results.getString("stocks.name");
            String symbol = results.getString("stocks.symbol");
            double price = results.getDouble("stocks.price");
            double shares = results.getDouble("stocks.shares");
            return new Stock(id, name, symbol, price, shares);
        }
        return null;
    }

    static ArrayList<Stock> selectStocks(Connection conn, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stocks INNER JOIN users ON stocks.user_id = users.id WHERE stocks.user_id = ?");
        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String symbol = results.getString("symbol");
            double price = results.getDouble("price");
            double shares = results.getDouble("shares");
            Stock stock = new Stock(id, name, symbol, price, shares);
            stocks.add(stock);
        }
        return stocks;
    }

    static void deleteStock (Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM stocks WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    static void updateStock (Connection conn, int id, String name, String symbol, double price, double shares /*int userId*/) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE stocks SET name = ?, symbol = ?, price = ?, shares = ? WHERE id = ?");
        stmt.setString(1, name);
        stmt.setString(2, symbol);
        stmt.setDouble(3, price);
        stmt.setDouble(4, shares);
        //stmt.setInt(5, userId);
        stmt.setInt(5, id);
        stmt.execute();
    }






    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);




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
                        User user = selectUser(conn, username);
                        m.put("stocks", selectStocks(conn, user.id));

                        return new ModelAndView(m, "stocks.html");
                    }

                },
                new MustacheTemplateEngine()
        );

        Spark.get (
                "/edit",
                (request, response) -> {

                    Session session = request.session();
                    String username = session.attribute("username");

                    int id = Integer.valueOf(request.queryParams("id"));
                    User user = selectUser(conn, username);

                    HashMap m = new HashMap<>();

                    m.put("username", username );
                    m.put("id", id);
                    m.put("stocks", selectStocks(conn, user.id));
                    return new ModelAndView(m, "edit.html");

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String pass = request.queryParams("password");
                    if (username == null || pass == null) {
                        throw new Exception("Name or pass not sent");
                    }

                    User user = selectUser(conn, username);
                    if (user == null) {
                        insertUser(conn, username, pass);
                    } else if (!pass.equals(user.password)) {
                        throw new Exception("Wrong password");
                    }

                    Session session = request.session();
                    session.attribute("username", username);

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


                    if (name == null ||  symbol == null || priceStr == null || sharesStr == null ) {
                        throw new Exception("Invalid form fields");
                    }

                    double price = Double.valueOf(priceStr);
                    double shares = Double.valueOf(sharesStr);

                    double value = price * shares;


                    User user = selectUser(conn, username);

                    if (user == null) {
                        throw new Exception("User does not exist");
                    }

                    //Stock s  =  new Stock(name, symbol, price, shares,  value);
                    //user.stocks.add(s);
                    insertStock(conn, name, symbol, price, shares, user.id);

                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/edit-stock",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    String name = request.queryParams("editname");
                    String symbol = request.queryParams("editsymbol");
                    String priceStr = request.queryParams("editprice");
                    String sharesStr = request.queryParams("editshares");
                    String idStr = request.queryParams("editid");

                    if (name == null ||  symbol == null || priceStr == null || sharesStr == null ) {
                        throw new Exception("Invalid form fields");
                    }

                    double price = Double.valueOf(priceStr);
                    double shares = Double.valueOf(sharesStr);
                    double value = price * shares;
                    int id = Integer.valueOf(idStr);


                    User user = selectUser(conn, username);

                    if (user == null) {
                        throw new Exception("User does not exist");
                    }
                    updateStock(conn, id, name, symbol, price, shares);


                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/delete-stock",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    User user = selectUser(conn, username);

                    int id = Integer.valueOf(request.queryParams("id"));
                    if (id < 0 ) {
                        throw new Exception("Invalid ID");
                    }
                    Integer.valueOf(id);

                    deleteStock(conn, id);

                    response.redirect("/");
                    return "";
                }
        );


    }
}
