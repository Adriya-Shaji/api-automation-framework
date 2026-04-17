package com.adriyashaji.automation.api;

import com.adriyashaji.automation.base.BaseTest;
import com.adriyashaji.automation.models.Film;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Tag("regression")
@DisplayName("Film API Tests")

public class FilmApiTest extends BaseTest {

   @Test
   @Tag("smoke")
   @DisplayName("GET all films and returns 200")
   void getAllFilm() {
      given().spec(getRequestSpec())
              .when()
              .get("/films")
              .then().log().all()
              .statusCode(200)
              .body("$", not(empty()))
              .time(lessThan(2000L));
   }


   @Test
   @Tag("smoke")
   @DisplayName("GET film with ID as 1 and return 200")
   void getSingleFilm() {
      given().spec(getRequestSpec())
              .when()
              .get("/films/1")
              .then().log().all()
              .statusCode(200)
              .body("id", equalTo("1"))
              .body("title", not(emptyOrNullString()));
   }


   @Test
   @DisplayName("GET film with invalid ID and returns 404")
   void getFilmNotFound() {
      given().spec(getRequestSpec())
              .when()
              .get("/films/9999")
              .then()
              .statusCode(404);
   }

   @ParameterizedTest(name = "GET /films/{0} should return 200")
   @CsvSource({"1", "2"})
   @DisplayName("Get films with ID 1 and 2 - returns 200")
   void getFilmParameterized(String id) {
      given().spec(getRequestSpec())
              .when()
              .get("/films/" + id)
              .then()
              .statusCode(200);
   }


   @Test
   @Tag("smoke")
   @DisplayName("POST a film - returns 201 and id created")
   void createFilm() {
      Film film = new Film(null, "Inception", "Christopher Nolan", 2010);

      // WireMock returns a static stub response — this test validates contract
      // shape (201, id present, title field exists), not server echo behaviour.
      // Against a real API, the stub would be replaced by the live endpoint.
      given().spec(getRequestSpec())
              .body(film)
              .when().post("/films")
              .then()
              .statusCode(201)
              .body("id", notNullValue())
              .body("title", equalTo("Inception"));
   }


   @Test
   @DisplayName("POST a film - returns 201 and extract created  film ID")
   void extractCreatedFilmId(){
      Film film = new Film(null, "Dune", "Denis Villeneuve", 2021);

      String id = given().spec(getRequestSpec())
              .body(film)
              .when().post("/films")
              .then()
              .statusCode(201)
              .extract().jsonPath().getString("id");

      assertThat(id).as("Created film ID should not be null").isNotNull();
   }


   @Test
   @DisplayName("Update a film - returns 200")
   void updateFilm() {
      Film film = new Film(
             "1", "Inception updated", "Christopher Nolan", 2010);

      given().spec(getRequestSpec())
              .body(film)
              .when()
              .put("/films/1")
              .then()
              .statusCode(200)
              .body("title", equalTo("Inception updated"));
   }


   @Test
   @DisplayName("Verify if response matches JSON schema - returns 200")
   void getFilmMatchesSchema() {
      given().spec(getRequestSpec())
              .when()
              .get("/films/1")
              .then()
              .statusCode(200)
              .body(matchesJsonSchemaInClasspath(
                      "schemas/film-schema.json"));
   }


   @Test
   @DisplayName("Delete a film - returns 200")
   void deleteFilm() {
      given().spec(getRequestSpec())
              .when()
              .delete("/films/1")
              .then()
              .statusCode(200);
   }
}

