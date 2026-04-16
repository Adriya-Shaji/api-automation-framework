package com.adriyashaji.automation.stubs;

import com.adriyashaji.automation.utils.ConfigReader;
import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AuthStubs {

    private static final String VALID_TOKEN = "test-bearer-token-123";
    private static final String VALID_USERNAME = ConfigReader.get("auth.username");
    private static final String VALID_PASSWORD = ConfigReader.get("auth.password");

    public static void register(WireMockServer wireMock) {

        wireMock.stubFor(post(urlEqualTo("/login"))
                .atPriority(2)
                .withRequestBody(matchingJsonPath(
                        "$[?(@.username == '" + VALID_USERNAME + "')]"))
                .withRequestBody(matchingJsonPath(
                        "$[?(@.password == '" + VALID_PASSWORD + "')]"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"token\": \"" + VALID_TOKEN + "\" }")));

        // Valid token - checked first, most specific happy path
        wireMock.stubFor(get(urlEqualTo("/secure/users"))
                .atPriority(1)
                .withHeader("Authorization", equalTo("Bearer " + VALID_TOKEN))
                .willReturn(okJson(
                        "{ \"users\": [{ \"id\": \"1\", \"name\": \"Leanne Graham\" }] }")));

        // Wrong token - 403
        wireMock.stubFor(get(urlEqualTo("/secure/users"))
                .atPriority(2)
                .withHeader("Authorization", equalTo("Bearer wrong-token"))
                .willReturn(aResponse().withStatus(403)));

        // No token - 401
        wireMock.stubFor(get(urlEqualTo("/secure/users"))
                .atPriority(3)
                .withHeader("Authorization", absent())
                .willReturn(aResponse().withStatus(401)));
    }
}