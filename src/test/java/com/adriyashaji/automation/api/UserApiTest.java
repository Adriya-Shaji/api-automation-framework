package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;

import com.adriyashaji.automation.models.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest extends BaseTest {


    @Test
    @Order(1)
    @Tag("smoke")
    @DisplayName("GET all users returns 200 with data array")
    void getAllUsers() {
        given(requestSpec)
                .when()
                .get("/users")
                .then().log().all()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .time(lessThan(2000L));
    }


    @Test
    @Order(2)
    @Tag("regression")
    @DisplayName("GET single user matches json schema")
    void getUserMatchesSchema(){
        given(requestSpec)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test
    @Order(3)
    @Tag("smoke")
    @DisplayName("GET single user by valid ID returns 200")
    void getSingleUser() {
        given(requestSpec)
                .when().get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"))
                .body("email", containsString("@"))
                .body("name", not(emptyString()));
    }


    @Test
    @Order(4)
    @Tag("regression")
    @DisplayName("GET non-existant user returns 404")
    void getUserNotFound() {
        given(requestSpec)
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
        given(requestSpec)
                .when().get("/users/" + id)
                .then()
                .statusCode(expectedStatus);
    }


    @Test
    @Order(5)
    @Tag("smoke")
    @DisplayName("POST create user returns 201 with generated ID")
    void createUser() {
        User user = new User("Janet QA", "janetqa", "janet@test.com");

        given(requestSpec)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("Janet QA"))
                .body("id", notNullValue());
    }


    @Test
    @Order(6)
    @Tag("regression")
    @DisplayName("POST create user and extract generated id")
    void extractCreatedUserId() {
        User user = new User("Test User", "testuser", "test@test.com");

        String id = given(requestSpec)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        assertThat(id).isNotNull().isNotBlank();
    }

    @Test
    @Order(7)
    @DisplayName("POST with missing name returns 400")
    void createUserWithMissingNameReturns400() {
        // sending user with no name — blank string
        User invalidUser = new User("", "janetqa", "janet@test.com");

        given(requestSpec)
                .body(invalidUser)
                .when().post("/users")
                .then()
                .statusCode(400)
                .body("error", equalTo("name is required"));
    }

    @Test
    @Order(8)
    @Tag("regression")
    @DisplayName("PUT update user returns 200 with updated data")
    void updateUser() {
        User user = new User("Janet Updated", "janetupdated", "janet.updated@test.com");

        given(requestSpec)
                .body(user)
                .when().put("/users/1")
                .then().statusCode(200)
                .body("name", equalTo("Janet Updated"))
                .body("id", equalTo("1"));
    }

    @Test
    @Order(9)
    @Tag("regression")
    @DisplayName("DELETE user returns 200")
    void deleteUser() {
        given(requestSpec)
                .when().delete("/users/1")
                .then()
                .statusCode(200);
    }

}