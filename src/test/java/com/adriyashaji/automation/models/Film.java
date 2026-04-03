package com.adriyashaji.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Film {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("director")
    private String director;

    @JsonProperty("year")
    private int year;

    public Film() {}

    public Film(String id, String title, String director, int year) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public int getYear() { return year; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDirector(String director) { this.director = director; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return "Film{id=" + id + ", title=" + title +
                ", director=" + director + ", year=" + year + "}";
    }
}