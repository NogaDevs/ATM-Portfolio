package com.atm.dao;

import resources.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final DatabaseConfig config = new DatabaseConfig();

    public static Connection connect() throws SQLException {
        try {
            return DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUsername(),
                    config.getDbPassword()
            );
        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
