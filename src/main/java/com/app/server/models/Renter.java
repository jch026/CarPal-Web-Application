package com.app.server.models;

public class Renter {

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getLicense() {
        return license;
    }
    public String getEmail() {
        return email;
    }

    String id=null;
    String firstName;
    String lastName;
    String username;
    String phoneno;
    String license;
    String email;

    public Renter(String firstName, String lastName, String username, String phoneno, String license, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneno = phoneno;
        this.license = license;
        this.email = email;
    }
    public void setId(String id) {
        this.id = id;
    }
}
