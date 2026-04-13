package com.adriyashaji.automation.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.adriyashaji.automation.utils.ConfigReader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.qameta.allure.restassured.AllureRestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;

public class BaseTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        configureFor("localhost", 8080);

        RestAssured.baseURI = ConfigReader.get("base.url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())
                .build();

        setupStubs();
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    protected static RequestSpecification requestSpec;

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

        // Valid - name exists AND is not blank
        stubFor(post(urlEqualTo("/users"))
                .withRequestBody(matchingJsonPath("$[?(@.name != '')]"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"6\", \"name\": \"Janet QA\", \"email\": \"janet@test.com\" }")));

        // Invalid - name is blank or missing
        stubFor(post(urlEqualTo("/users"))
                .withRequestBody(matchingJsonPath("$[?(@.name == '')]"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"name is required\" }")));

        // PUT update user
        stubFor(put(urlEqualTo("/users/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"name\": \"Janet Updated\", \"username\": \"janetupdated\", \"email\": \"janet.updated@test.com\" }")));

        // DELETE user
        stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse().withStatus(200)));


        // Login endpoint - returns a token when correct credentials sent
        stubFor(post(urlEqualTo("/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"token\": \"test-bearer-token-123\" }")));

        // Protected endpoint - valid token returns 200
        stubFor(get(urlEqualTo("/secure/users"))
                .withHeader("Authorization", equalTo("Bearer test-bearer-token-123"))
                .willReturn(okJson("{ \"users\": [{ \"id\": \"1\", \"name\": \"Leanne Graham\" }] }")));

        // No token - 401
        stubFor(get(urlEqualTo("/secure/users"))
                .withHeader("Authorization", absent())
                .willReturn(aResponse().withStatus(401)));

        // Wrong token - 403
        stubFor(get(urlEqualTo("/secure/users"))
                .withHeader("Authorization", equalTo("Bearer wrong-token"))
                .willReturn(aResponse().withStatus(403)));

        // Films endpoints
        stubFor(get(urlEqualTo("/films"))
                .willReturn(okJson("[{ \"id\": \"1\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2010 }," +
                        "{ \"id\": \"2\", \"title\": \"The Dark Knight\", \"director\": \"Christopher Nolan\", \"year\": 2008 }," +
                        "{ \"id\": \"3\", \"title\": \"Interstellar\", \"director\": \"Christopher Nolan\", \"year\": 2014 }]")));

        stubFor(get(urlEqualTo("/films/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2010 }")));

        stubFor(get(urlEqualTo("/films/2"))
                .willReturn(okJson("{ \"id\": \"2\", \"title\": \"The Dark Knight\", \"director\": \"Christopher Nolan\", \"year\": 2008 }")));

        stubFor(get(urlEqualTo("/films/9999"))
                .willReturn(aResponse().withStatus(404)));

        stubFor(post(urlEqualTo("/films"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"4\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2017 }")));

        stubFor(put(urlEqualTo("/films/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"title\": \"Inception updated\", \"director\": \"Christopher Nolan\", \"year\": 2010 }")));

        stubFor(delete(urlEqualTo("/films/1"))
                .willReturn(aResponse().withStatus(200)));
    }
}