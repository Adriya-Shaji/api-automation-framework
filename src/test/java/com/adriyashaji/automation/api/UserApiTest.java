package com.adriyashaji.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class UserApiTest {
    @BeforeAll
    static void setup() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        RestAssured.baseURI = "http://localhost:3000";
      //  RestAssured.baseURI = "https://jsonplaceholder.typicode";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    @Tag("smoke")
    @DisplayName("GET all users returns 200 with data array")
    void getAllUsers() {
        given().log().all()
                .when()
                .get("/users")
                .then().log().all()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .time(lessThan(2000L));
    }


    @Test
    @Order(2)
    @Tag("smoke")
    @DisplayName("GET single user by valid ID returns 200")
    void getSingleUser() {
        given()
                .when().get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"))
                .body("email", containsString("@"))
                .body("name", not(emptyString()));
    }


    @Test
    @Order(3)
    @Tag("regression")
    @DisplayName("GET non-existant user returns 404")
    void getUserNotFound() {
        given()
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
        given()
                .when().get("/users/" + id)
                .then()
                .statusCode(expectedStatus);
    }

    @Test
    @Order(4)
    @Tag("smoke")
    @DisplayName("POST create user returns 201 with generated ID")
    void createUser() {
        String requestBody = "{\"name\": \"Janet QA\", \"username\": \"janetqa\", \"email\": \"janet@test.com\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("Janet QA"))
                .body("id", notNullValue());
    }


    @Test
    @Order(5)
    @Tag("regression")
    @DisplayName("POST create user and extract generated id")
    void extractCreatedUserId() {
        String requestBody = "{\"name\": \"Test User\", \"username\": \"testuser\", \"email\": \"test@test.com\"}";

        String id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/users")
                .then()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        assertThat(id).isNotNull().isNotBlank();
    }

    @Test
    @Order(6)
    @Tag("regression")
    @DisplayName("PUT update user returns 200 with updated data")
    void updateUser() {
        String requestBody = "{\"name\": \"Janet Updated\", \"username\": \"janetupdated\", \"email\": \"janet.updated@test.com\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().put("/users/1")
                .then().statusCode(200)
                .body("name", equalTo("Janet Updated"))
                .body("id", equalTo("1"));
    }

    @Test
    @Order(7)
    @Tag("regression")
    @DisplayName("DELETE user returns 200")
    void deleteUser() {
        given()
                .when().delete("/users/1")
                .then()
                .statusCode(200);
    }

}