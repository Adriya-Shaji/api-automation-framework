package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;

import com.adriyashaji.automation.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UserApiTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("GET all users returns 200 with data array")
    void getAllUsers() {
        given().spec(getRequestSpec())
                .when()
                .get("/users")
                .then().log().all()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .time(lessThan(2000L));
    }


    @Test
    @Tag("regression")
    @DisplayName("GET single user matches json schema")
    void getUserMatchesSchema(){
        given().spec(getRequestSpec())
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET single user by valid ID returns 200")
    void getSingleUser() {
        given().spec(getRequestSpec())
                .when().get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("email", containsString("@"))
                .body("name", not(emptyString()));
    }


    @Test
    @Tag("regression")
    @DisplayName("GET non-existent user returns 404")
    void getUserNotFound() {
        given().spec(getRequestSpec())
                .when().get("/users/9999")
                .then()
                .statusCode(404);
    }


    @ParameterizedTest(name = "User ID {0} should return status {1}")
    @CsvSource({
            "2, 200",
            "3, 200",
            "9999, 404"
    })
    @Tag("regression")
    void getUserParameterized(int id, int expectedStatus) {
        given().spec(getRequestSpec())
                .when().get("/users/" + id)
                .then()
                .statusCode(expectedStatus);
    }


    @Test
    @Tag("smoke")
    @DisplayName("POST create user returns 201 with generated ID")
    void createUser() throws JsonProcessingException {
        User user = new User("Test User", "testuser", "test@example.com");
        given().spec(getRequestSpec())
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test User"))
                .body("email", equalTo("test@example.com"));
    }


    @Test
    @Tag("regression")
    @DisplayName("POST create user and extract generated id")
    void extractCreatedUserId() {
        User user = new User("Test User", "testuser", "test@example.com");

        int id = given().spec(getRequestSpec())
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test User"))
                .extract().jsonPath().getInt("id");

        //confirms it's a real generated ID
        assertThat(id).isPositive();
    }

    @Test
    @DisplayName("POST with missing name returns 400")
    void createUserWithMissingNameReturns400() {
        // sending user with no name — blank string
        User invalidUser = new User("", "janetqa", "janet@test.com");

        given().spec(getRequestSpec())
                .body(invalidUser)
                .when().post("/users")
                .then()
                .statusCode(400)
                .body("error", equalTo("name, username and email are required"));
    }

    @Test
    @Tag("regression")
    @DisplayName("PUT update user returns 200 with updated data")
    void updateUser() {
        User user = new User("Janet Updated", "janetupdated", "janet.updated@test.com");

        given().spec(getRequestSpec())
                .body(user)
                .when().put("/users/1")
                .then().statusCode(200)
                .body("name", equalTo("Janet Updated"))
                .body("id", equalTo(1));
    }

    @Test
    @Tag("regression")
    @DisplayName("DELETE user returns 200")
    void deleteUser() {
        given().spec(getRequestSpec())
                .when().delete("/users/1")
                .then()
                .statusCode(200);
    }

}