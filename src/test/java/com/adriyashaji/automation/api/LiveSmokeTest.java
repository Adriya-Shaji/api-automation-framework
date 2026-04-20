package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("live")
@DisplayName("Live contract smoke tests")
public class LiveSmokeTest extends BaseTest {

    @Test
    @DisplayName("GET /users returns 200 and contract fields")
    void getUsersContractCheck() {
        given().spec(getRequestSpec())
                .when().get("/users")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("[0].id", notNullValue())
                .body("[0].email", containsString("@"))
                .body("[0].name", notNullValue());
    }

    @Test
    @DisplayName("GET /users/1 returns expected contract shape")
    void getSingleUserContractCheck() {
        given().spec(getRequestSpec())
                .when().get("/users/1")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("email", containsString("@"));
    }

    // Only add 404 test after manually verifying in Postman
}