package com.adriyashaji.automation.utils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Set;

public class DatabaseHelper {
    private Connection connection;

    private static final Set<String> ALLOWED_TABLES = Set.of("users", "payments");
    private static final Set<String> ALLOWED_COLUMNS = Set.of("id", "name", "email", "customer_id", "payment_id", "amount");

    private void validateIdentifier(String value, Set<String> allowed, String type) {
        if (!allowed.contains(value.toLowerCase())) {
            throw new IllegalArgumentException("Invalid " + type + ": " + value);
        }
    }

    public DatabaseHelper(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        try {
            createTestData();
        } catch (SQLException e) {
            connection.close();
            throw e;
        }    }

    // Sets up the H2 in-memory table + seed data
    // MERGE = insert if not exists, update if exists
    private void createTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users " +
                            "(id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100))"
            );
            stmt.execute(
                    "MERGE INTO users VALUES (1, 'Adriya Shaji', 'adriya@test.com')"
            );
            stmt.execute(
                    "MERGE INTO users VALUES (2, 'John Doe', 'john@test.com')"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS payments " +
                            "(payment_id INT PRIMARY KEY, " +
                            " customer_id INT, " +
                            " amount DECIMAL(10,2), " +
                            " payment_date TIMESTAMP)"
            );
            stmt.execute(
                    "MERGE INTO payments VALUES (101, 1, 99.99, NOW())"
            );
        }
    }

    public int getRowCount(String tableName) throws SQLException {
        validateIdentifier(tableName, ALLOWED_TABLES, "table");
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public boolean userRecordExists(String tableName, String column,
                                    String value) throws SQLException {
        validateIdentifier(tableName, ALLOWED_TABLES, "table");
        validateIdentifier(column, ALLOWED_COLUMNS, "column");
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + column + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean validatePayment(int paymentId, BigDecimal expectedAmount,
                                   int expectedCustomerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payments p " +
                "INNER JOIN users u ON p.customer_id = u.id " +
                "WHERE p.payment_id = ? " +
                "AND p.amount = ? " +
                "AND p.customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setBigDecimal(2, expectedAmount);
            ps.setInt(3, expectedCustomerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Timestamp window check — catches silent failures where payment exists
    // but date field is wrong (epoch 0, future-dated, clock skew).
    // DATEADD is H2-specific. PostgreSQL equivalent: NOW() - INTERVAL '? seconds'
    public boolean paymentExistsWithinTimeWindow(int paymentId,
                                                 int bufferSeconds) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payments " +
                "WHERE payment_id = ? " +
                "AND payment_date BETWEEN " +
                "DATEADD('SECOND', -?, NOW()) AND DATEADD('SECOND', ?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, bufferSeconds);
            ps.setInt(3, bufferSeconds);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}