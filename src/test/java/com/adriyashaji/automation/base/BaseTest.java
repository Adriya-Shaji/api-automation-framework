package com.adriyashaji.automation.base;

import com.adriyashaji.automation.stubs.AuthStubs;
import com.adriyashaji.automation.stubs.FilmStubs;
import com.adriyashaji.automation.stubs.UserStubs;
import com.adriyashaji.automation.utils.ConfigReader;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.qameta.allure.restassured.AllureRestAssured;

public class BaseTest {

    private static WireMockServer wireMockServer;
    private static RequestSpecification requestSpec;

    protected static RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    @BeforeAll
    static void setup() {
        String mode = System.getProperty("mode", "mock");
        String baseUrl;

        if ("mock".equals(mode)) {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
            wireMockServer.start();
            setupStubs();
            baseUrl = "http://localhost:" + wireMockServer.port();
        } else {
            wireMockServer = null;
            baseUrl = ConfigReader.get("base.url");
        }

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @AfterAll
    static void teardown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private static void setupStubs() {
        UserStubs.register(wireMockServer);
        FilmStubs.register(wireMockServer);
        AuthStubs.register(wireMockServer);
    }
}