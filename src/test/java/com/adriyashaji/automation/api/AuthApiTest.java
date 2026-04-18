package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;
import com.adriyashaji.automation.utils.AuthManager;
import com.adriyashaji.automation.stubs.AuthStubs;
import com.adriyashaji.automation.utils.ConfigReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
        given().spec(getRequestSpec())
                .body(Map.of(
                        "username", ConfigReader.get("auth.username"),
                        "password", ConfigReader.get("auth.password")
                ))                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("GET secure endpoint with valid token returns 200")
    void authenticatedRequestReturns200() {
        String token = AuthManager.getToken(getRequestSpec());

        given().spec(getRequestSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/secure/users")
                .then()
                .statusCode(200)
                .body("users", notNullValue())
                .body("users[0].id", equalTo("1"))
                .body("users[0].name", equalTo("Leanne Graham"));
    }

    @Test
    @DisplayName("GET secure endpoint with no token returns 401")
    void noTokenReturns401() {
        given().spec(getRequestSpec())
                .when()
                .get("/secure/users")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("GET secure endpoint with wrong token returns 403")
    void wrongTokenReturns403() {
        given().spec(getRequestSpec())
                .header("Authorization", "Bearer " + AuthStubs.INVALID_TOKEN)
                .when()
                .get("/secure/users")
                .then()
                .statusCode(403);
    }


    @Test
    @DisplayName("POST login with invalid credentials returns 401")
    void invalidLoginReturns401() {
        given().spec(getRequestSpec())
                .body(Map.of(
                        "username", "wrong-user",
                        "password", "wrong-pass"
                ))
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("error", notNullValue());
    }
}