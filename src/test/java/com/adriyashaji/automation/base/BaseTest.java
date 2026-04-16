package com.adriyashaji.automation.base;

import com.adriyashaji.automation.stubs.AuthStubs;
import com.adriyashaji.automation.stubs.FilmStubs;
import com.adriyashaji.automation.stubs.UserStubs;
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

public class BaseTest {

    protected static WireMockServer wireMockServer;
    protected static RequestSpecification requestSpec;

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

    private static void setupStubs() {
        UserStubs.register(wireMockServer);
        FilmStubs.register(wireMockServer);
        AuthStubs.register(wireMockServer);
    }
}