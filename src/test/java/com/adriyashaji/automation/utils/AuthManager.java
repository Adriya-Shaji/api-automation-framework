package com.adriyashaji.automation.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

public class AuthManager {

    //cachedToken - one token shared across entire test run
    private static String cachedToken = null;

    public static String getToken() {
        if (cachedToken == null) {
            String username = System.getenv("AUTH_USERNAME") != null
                    ? System.getenv("AUTH_USERNAME")
                    : ConfigReader.get("auth.username");

            String password = System.getenv("AUTH_PASSWORD") != null
                    ? System.getenv("AUTH_PASSWORD")
                    : ConfigReader.get("auth.password");

            Map<String, String> credentials = Map.of(
                    "username", username,
                    "password", password
            );

            cachedToken = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(credentials)
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