package com.hotel.room;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    private static final String URL  = "jdbc:postgresql://localhost:5432/hotel_room";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Driver PostgreSQL chargé");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Impossible de charger le driver PostgreSQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
