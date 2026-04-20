package com.adriyashaji.automation.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FilmStubs {

    public static void register(WireMockServer wireMock) {

        wireMock.stubFor(get(urlEqualTo("/films"))
                .willReturn(okJson(
                        "[{ \"id\": \"1\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2010 }," +
                                "{ \"id\": \"2\", \"title\": \"The Dark Knight\", \"director\": \"Christopher Nolan\", \"year\": 2008 }," +
                                "{ \"id\": \"3\", \"title\": \"Interstellar\", \"director\": \"Christopher Nolan\", \"year\": 2014 }]")));

        // 404 registered before the parameterised GET to prevent /films/9999 matching a broader pattern
        wireMock.stubFor(get(urlEqualTo("/films/9999"))
                .atPriority(1)
                .willReturn(aResponse().withStatus(404)));

        wireMock.stubFor(get(urlEqualTo("/films/1"))
                .willReturn(okJson(
                        "{ \"id\": \"1\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2010 }")));

        wireMock.stubFor(get(urlEqualTo("/films/2"))
                .willReturn(okJson(
                        "{ \"id\": \"2\", \"title\": \"The Dark Knight\", \"director\": \"Christopher Nolan\", \"year\": 2008 }")));

        // Stub for createFilm() — posts Inception
        wireMock.stubFor(post(urlEqualTo("/films"))
                .atPriority(1)
                .withRequestBody(matchingJsonPath("$[?(@.title == 'Inception')]"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"4\", \"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"year\": 2010 }")));

        // Stub for extractCreatedFilmId() — posts Dune
        wireMock.stubFor(post(urlEqualTo("/films"))
                .atPriority(2)
                .withRequestBody(matchingJsonPath("$[?(@.title == 'Dune')]"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"5\", \"title\": \"Dune\", \"director\": \"Denis Villeneuve\", \"year\": 2021 }")));

        wireMock.stubFor(put(urlEqualTo("/films/1"))
                .willReturn(okJson(
                        "{ \"id\": \"1\", \"title\": \"Inception updated\", \"director\": \"Christopher Nolan\", \"year\": 2010 }")));

        wireMock.stubFor(delete(urlEqualTo("/films/1"))
                .willReturn(aResponse().withStatus(200)));
    }
}