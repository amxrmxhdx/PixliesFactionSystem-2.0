package me.mickmmars.factions.factions.sqlite;

import java.sql.*;
import java.util.StringJoiner;

public class SQLiteManager {

    private String url;

    public SQLiteManager(String path) {
        this.url = "jdbc:sqlite:" + path;
    }

    public void create() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeStatement(String statement) {
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            stmt.execute(statement);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection connect() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
