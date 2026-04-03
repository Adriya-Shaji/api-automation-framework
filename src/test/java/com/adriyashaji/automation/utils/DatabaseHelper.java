package com.adriyashaji.automation.utils;

import org.junit.jupiter.api.AfterAll;

import java.sql.*;

public class DatabaseHelper {
    private Connection connection;

    public DatabaseHelper() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        createTestData();
    }

    private void createTestData() throws SQLException {
        //Create table - users
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS users " +
                        "(id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100))"
        );

        //Inserts/Merges user 1
        connection.createStatement().execute(
                "MERGE INTO users VALUES (1, 'Adriya Shaji', 'adriya@test.com')"
        );

        //Inserts/Merges user 2
        connection.createStatement().execute(
                "MERGE INTO users VALUES (2, 'John Doe', 'john@test.com')"
        );
    }

    public int getRowCount(String tableName) throws SQLException{
        PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT (*)  FROM " + tableName
        );
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public boolean recordExists(String tableName, String column, String value)
        throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT (*) FROM " + tableName + " WHERE "
                        + column + " = ?"
        );
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public void close() throws SQLException {
        if (connection !=null)
            connection.close();
    }

}
