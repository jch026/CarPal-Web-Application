package com.app.server.models;

public class Owner {

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getLicense() { return license; }

    public String getAccountNumber() { return accountNumber; }

    String id=null;
    String firstName;
    String lastName;
    String phoneNumber;
    String username;
    String email;
    String license;
    String accountNumber;



    public Owner(String firstName, String lastName, String phoneNumber, String username, String email, String license, String accountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.email = email;
        this.license = license;
        this.accountNumber = accountNumber;

    }
    public void setId(String id) {
        this.id = id;
    }
}