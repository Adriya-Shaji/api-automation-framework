package com.adriyashaji.automation.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BaseTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        configureFor("localhost", 8080);

        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        setupStubs();
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    private static void setupStubs() {

        // GET all users
        stubFor(get(urlEqualTo("/users"))
                .willReturn(okJson("[" +
                        "{ \"id\": \"1\", \"name\": \"Leanne Graham\", \"username\": \"Bret\", \"email\": \"sincere@april.biz\" }," +
                        "{ \"id\": \"2\", \"name\": \"Ervin Howell\", \"username\": \"Antonette\", \"email\": \"shanna@melissa.tv\" }," +
                        "{ \"id\": \"3\", \"name\": \"Clementine Bauch\", \"username\": \"Samantha\", \"email\": \"nathan@yesenia.net\" }," +
                        "{ \"id\": \"4\", \"name\": \"Patricia Lebsack\", \"username\": \"Karianne\", \"email\": \"julianne@kory.org\" }," +
                        "{ \"id\": \"5\", \"name\": \"Chelsey Dietrich\", \"username\": \"Kamren\", \"email\": \"lucio@annie.ca\" }" +
                        "]")));

        // GET single user
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"name\": \"Leanne Graham\", \"username\": \"Bret\", \"email\": \"sincere@april.biz\" }")));

        stubFor(get(urlEqualTo("/users/2"))
                .willReturn(okJson("{ \"id\": \"2\", \"name\": \"Ervin Howell\", \"username\": \"Antonette\", \"email\": \"shanna@melissa.tv\" }")));

        stubFor(get(urlEqualTo("/users/3"))
                .willReturn(okJson("{ \"id\": \"3\", \"name\": \"Clementine Bauch\", \"username\": \"Samantha\", \"email\": \"nathan@yesenia.net\" }")));

        // GET 404
        stubFor(get(urlEqualTo("/users/9999"))
                .willReturn(aResponse().withStatus(404)));

        // POST create user
        stubFor(post(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"6\", \"name\": \"Janet QA\", \"username\": \"janetqa\", \"email\": \"janet@test.com\" }")));

        // PUT update user
        stubFor(put(urlEqualTo("/users/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"name\": \"Janet Updated\", \"username\": \"janetupdated\", \"email\": \"janet.updated@test.com\" }")));

        // DELETE user
        stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse().withStatus(200)));
    }
}