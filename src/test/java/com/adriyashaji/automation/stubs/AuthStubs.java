package com.adriyashaji.automation.stubs;

import com.adriyashaji.automation.utils.ConfigReader;
import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AuthStubs {

    public static final String VALID_USERNAME = "testuser";
    public static final String VALID_PASSWORD = "testpass123";
    public static final String VALID_TOKEN = "test-bearer-token-123";
    public static final String INVALID_TOKEN = "wrong-token";

            public static void register(WireMockServer wireMock) {

                // Valid credentials — full body match
                // equalToJson is safe: no injection surface if values have special characters
                // ignoreArrayOrder=true, ignoreExtraElements=true so future client fields don't break it
                wireMock.stubFor(post(urlEqualTo("/login"))
                        .atPriority(2)
                        .withRequestBody(equalToJson(
                                "{\"username\": \"" + VALID_USERNAME + "\", \"password\": \"" + VALID_PASSWORD + "\"}",
                                true,  // ignoreArrayOrder
                                true   // ignoreExtraElements
                        ))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"token\": \"" + VALID_TOKEN + "\" }")));

                // Catch-all: any login with wrong credentials falls through to here - given leat priority
                wireMock.stubFor(post(urlEqualTo("/login"))
                        .atPriority(5)
                        .willReturn(aResponse()
                                .withStatus(401)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"error\": \"Invalid credentials\" }")));

                // Exact valid token - checked first, most specific happy path
                wireMock.stubFor(get(urlEqualTo("/secure/users"))
                        .atPriority(1)
                        .withHeader("Authorization", equalTo("Bearer " + VALID_TOKEN))
                        .willReturn(okJson(
                                "{ \"users\": [{ \"id\": \"1\", \"name\": \"Leanne Graham\" }] }")));

                // Wrong/expired token - 403
                wireMock.stubFor(get(urlEqualTo("/secure/users"))
                        .atPriority(2)
                        .withHeader("Authorization", equalTo("Bearer " + INVALID_TOKEN))
                        .willReturn(aResponse().withStatus(403)));

                // No token/authorization header - 401
                wireMock.stubFor(get(urlEqualTo("/secure/users"))
                        .atPriority(3)
                        .withHeader("Authorization", absent())
                        .willReturn(aResponse().withStatus(401)));

                // Catch-all: any unrecognised/unknown token → 403
                wireMock.stubFor(get(urlEqualTo("/secure/users"))
                        .atPriority(5)
                        .withHeader("Authorization", matching("Bearer .*"))
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"error\": \"Invalid token\" }")));
            }
}