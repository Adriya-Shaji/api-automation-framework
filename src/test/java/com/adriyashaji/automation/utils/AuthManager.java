package com.adriyashaji.automation.utils;

import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthManager {

    // Stateless utility. Token lifecycle is the caller's responsibility.
    public static String getToken(RequestSpecification spec) {
        String envUsername = System.getenv("AUTH_USERNAME");
        String username = envUsername != null ? envUsername : ConfigReader.get("auth.username");

        String envPassword = System.getenv("AUTH_PASSWORD");
        String password = envPassword != null ? envPassword : ConfigReader.get("auth.password");

        return given()
                .spec(spec)
                .body(Map.of(
                        "username", username,
                        "password", password))
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }
}