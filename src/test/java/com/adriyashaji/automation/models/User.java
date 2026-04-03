package com.adriyashaji.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    //empty constructor
    public User() {}

    //request constructor - the test writer
    public User(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    //Getters - read a field value of private fields
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    //Setters - to fill the object field by field
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name=" + name +
                ", username=" + username + ", email=" + email + "}";
    }
}