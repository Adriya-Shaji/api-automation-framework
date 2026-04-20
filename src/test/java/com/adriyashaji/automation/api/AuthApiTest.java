package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;
import com.adriyashaji.automation.utils.AuthManager;
import com.adriyashaji.automation.stubs.AuthStubs;
import com.adriyashaji.automation.utils.ConfigReader;
import org.junit.jupiter.api.BeforeAll;
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

    private static String authToken;

    @BeforeAll
    static void fetchToken() {
        authToken = AuthManager.getToken(getRequestSpec());
    }

    @Test
    @DisplayName("POST login returns 200 with valid token")
    void loginReturnsToken() {
        given().spec(getRequestSpec())
                .body(validCredentials())
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", equalTo(AuthStubs.VALID_TOKEN));
    }

    @Test
    @DisplayName("GET secure endpoint with valid token returns 200")
    void authenticatedRequestReturns200() {
        given().spec(getRequestSpec())
                .header("Authorization", "Bearer " + authToken)
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

    private Map<String, String> validCredentials() {
        String username = System.getenv("AUTH_USERNAME") != null
                ? System.getenv("AUTH_USERNAME")
                : ConfigReader.get("auth.username");
        String password = System.getenv("AUTH_PASSWORD") != null
                ? System.getenv("AUTH_PASSWORD")
                : ConfigReader.get("auth.password");
        return Map.of("username", username, "password", password);
    }
}