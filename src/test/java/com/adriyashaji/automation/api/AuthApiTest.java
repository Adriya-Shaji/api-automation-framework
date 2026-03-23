package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;
import com.adriyashaji.automation.utils.AuthManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Tag("smoke")
@Tag("regression")
@DisplayName("Auth API Tests")
public class AuthApiTest extends BaseTest {

    @BeforeEach
    void resetAuth() {
        AuthManager.resetToken();
    }

    @Test
    @DisplayName("POST login returns 200 with valid token")
    void loginReturnsToken() {
        given(requestSpec)
                .body("{ \"username\": \"adriya\", \"password\": \"secret123\" }")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("GET secure endpoint with valid token returns 200")
    void authenticatedRequestReturns200() {
        String token = AuthManager.getToken();

        given(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/secure/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("GET secure endpoint with no token returns 401")
    void noTokenReturns401() {
        given(requestSpec)
                .when()
                .get("/secure/users")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("GET secure endpoint with wrong token returns 403")
    void wrongTokenReturns403() {
        given(requestSpec)
                .header("Authorization", "Bearer wrong-token")
                .when()
                .get("/secure/users")
                .then()
                .statusCode(403);
    }
}