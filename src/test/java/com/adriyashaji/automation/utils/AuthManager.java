package com.adriyashaji.automation.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class AuthManager {

    //cachedToken - one token shared across entire test run
    private static String cachedToken = null;

    public static String getToken() {
        if (cachedToken == null) {
            cachedToken = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body("{ \"username\": \"adriya\", \"password\": \"secret123\" }")
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getString("token");
        }
        return cachedToken;
    }

    // Sets cachedToken back to null. Useful if you ever need to force a fresh login
    public static void resetToken() {
        cachedToken = null;
    }
}