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
        dbHelper = new DatabaseHelper(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
    }

    @Test
    @DisplayName("User record exists in database")
    void userRecordExists() throws SQLException{
        boolean exists = dbHelper.userRecordExists(
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


    @Test
    @DisplayName("Payment record exists with correct amount and customer")
    void paymentIsValidInDatabase() throws SQLException{
        boolean validPayment = dbHelper.validatePayment(101, 99.99, 1);

        Assertions.assertTrue(validPayment,
        "Payment 101 not found or data mismatch — " +
                "API may have returned 201 but DB write failed or corrupted");
    }


    @Test
    @DisplayName("Payment timestamp falls within expected window")
    void paymentDateIsWithinWindow() throws SQLException {
        boolean withinWindow = dbHelper.paymentExistsWithinTimeWindow(
                101, 5);
        Assertions.assertTrue(withinWindow,
                "Payment 101 exists but timestamp is outside expected window — " +
                        "possible clock skew or incorrect date field mapping");
    }



    @AfterAll
    static  void tearDown() throws SQLException{
        dbHelper.close();
    }
}
