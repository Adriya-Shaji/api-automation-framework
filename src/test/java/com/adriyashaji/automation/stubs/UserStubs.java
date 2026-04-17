package com.adriyashaji.automation.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class UserStubs {

    public static void register(WireMockServer wireMock) {

        wireMock.stubFor(get(urlEqualTo("/users"))
                .willReturn(okJson("[" +
                        "{ \"id\": \"1\", \"name\": \"Leanne Graham\", \"username\": \"Bret\", \"email\": \"sincere@april.biz\" }," +
                        "{ \"id\": \"2\", \"name\": \"Ervin Howell\", \"username\": \"Antonette\", \"email\": \"shanna@melissa.tv\" }," +
                        "{ \"id\": \"3\", \"name\": \"Clementine Bauch\", \"username\": \"Samantha\", \"email\": \"nathan@yesenia.net\" }," +
                        "{ \"id\": \"4\", \"name\": \"Patricia Lebsack\", \"username\": \"Karianne\", \"email\": \"julianne@kory.org\" }," +
                        "{ \"id\": \"5\", \"name\": \"Chelsey Dietrich\", \"username\": \"Kamren\", \"email\": \"lucio@annie.ca\" }" +
                        "]")));

        wireMock.stubFor(get(urlEqualTo("/users/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"name\": \"Leanne Graham\", \"username\": \"Bret\", \"email\": \"sincere@april.biz\" }")));

        wireMock.stubFor(get(urlEqualTo("/users/2"))
                .willReturn(okJson("{ \"id\": \"2\", \"name\": \"Ervin Howell\", \"username\": \"Antonette\", \"email\": \"shanna@melissa.tv\" }")));

        wireMock.stubFor(get(urlEqualTo("/users/3"))
                .willReturn(okJson("{ \"id\": \"3\", \"name\": \"Clementine Bauch\", \"username\": \"Samantha\", \"email\": \"nathan@yesenia.net\" }")));

        // Negative path — verify 404 handling, not just happy path
        wireMock.stubFor(get(urlEqualTo("/users/9999"))
                .willReturn(aResponse().withStatus(404)));

        // Priority 1: valid payload — all required fields present and non-whitespace
        // Regex match ensures blank strings don't slip through as valid
        wireMock.stubFor(post(urlEqualTo("/users"))
                .atPriority(1)
                .withRequestBody(matchingJsonPath("$[?(@.name != '')]"))
                .withRequestBody(matchingJsonPath("$[?(@.username != '')]"))
                .withRequestBody(matchingJsonPath("$[?(@.email != '')]"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"6\", \"name\": \"Janet QA\", \"username\": \"janetqa\", \"email\": \"janet@test.com\" }")));
        // Priority 2: catch-all for missing or blank fields — drives negative validation tests
        wireMock.stubFor(post(urlEqualTo("/users"))
                .atPriority(2)
                .withRequestBody(matchingJsonPath("$[?(@.name == '')]"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"name, username and email are required\" }")));

        wireMock.stubFor(put(urlEqualTo("/users/1"))
                .willReturn(okJson("{ \"id\": \"1\", \"name\": \"Janet Updated\", \"username\": \"janetupdated\", \"email\": \"janet.updated@test.com\" }")));

        wireMock.stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse().withStatus(200)));
    }
}