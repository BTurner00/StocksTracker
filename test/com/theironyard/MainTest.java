package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Ben on 6/14/16.
 */
public class MainTest {

    public Connection startConnection () throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser () throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        conn.close();
        assertTrue(user !=null);
    }

    @Test
    public void testStock () throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        Main.insertStock(conn, "ok", "yep", 2.0, 1.0, 1);
        Stock stock = Main.selectStock(conn, 1);
        conn.close();
        assertTrue(stock != null);
        assertTrue(stock.name.equals("ok"));
    }

    @Test
    public void testReplies () throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertUser(conn, "Bob", "");
        User alice = Main.selectUser(conn, "Alice");
        User bob = Main.selectUser(conn, "Bob");

        Main.insertStock(conn, "ok", "yes", 1.0, 2.0,  alice.id);
        Main.insertStock(conn, "yep", "no", 2.0, 3.0,  bob.id);
        Main.insertStock(conn, "si", "ko", 1.2, 4.0, bob.id);



        ArrayList<Stock> stocks = Main.selectStocks(conn, 1);
        conn.close();
        assertTrue(stocks.size() == 1);
    }

    @Test
    public void testDelete () throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User alice = Main.selectUser(conn, "Alice");
        Main.insertStock(conn, "ok", "yeah", 1.0, 1.0, alice.id );
        Main.deleteStock(conn, 1);
        Stock stock = Main.selectStock(conn,1);
        conn.close();
        assertTrue(stock == null);

    }

    /*@Test
    public void testUpdate () throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User alice = Main.selectUser(conn, "Alice");
        Main.insertStock(conn, "ok", "yeah", 1.0, 1.0, alice.id );
        Main.updateStock(conn, 1, "yeah", "ok", 2.0, 2.0, alice.id);
        Stock stock = Main.selectStock(conn, 1);
        conn.close();
        assertTrue(stock.name == "yeah");
    }


*/

}