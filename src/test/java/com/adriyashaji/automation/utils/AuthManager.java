package com.adriyashaji.automation.utils;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthManager {

    private static String cachedToken = null;

    public static String getToken(RequestSpecification spec) {
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

            cachedToken = given()
                    .spec(spec)
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

    public static void resetToken() {
        cachedToken = null;
    }
}