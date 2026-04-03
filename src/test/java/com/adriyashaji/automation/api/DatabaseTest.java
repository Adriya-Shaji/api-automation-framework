package com.adriyashaji.automation.api;


import com.adriyashaji.automation.utils.DatabaseHelper;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

@DisplayName("Database validation tests")
@Tag("database")
public class DatabaseTest {
    private static DatabaseHelper dbHelper;

    @BeforeAll
    static void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
    }

    @Test
    @DisplayName("User record exists in database")
    void userRecordExists() throws SQLException{
        boolean exists = dbHelper.recordExists(
                "users", "email", "adriya@test.com");
        Assertions.assertTrue(exists,
                "Expected user record to exist but it was not found");
    }


    //If someone accidentally adds a third `MERGE` to the helper,
    // this test catches it immediately.
    @Test
    @DisplayName("Users table has correct row count")
    void usersTableRowCount() throws SQLException{
        int count = dbHelper.getRowCount("users");
        Assertions.assertEquals(2, count, "Expected 2 users in the table");
    }

    @AfterAll
    static  void tearDown() throws SQLException{
        dbHelper.close();
    }
}
