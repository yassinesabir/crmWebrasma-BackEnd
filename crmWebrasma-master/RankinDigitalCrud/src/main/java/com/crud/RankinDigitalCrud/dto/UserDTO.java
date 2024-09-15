package com.crud.RankinDigitalCrud.dto;

public class UserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String name;

    // Constructor
    public UserDTO(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.name = firstName + " " + lastName; // Set fullName
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateName(); // Update fullName when firstName changes
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateName(); // Update fullName when lastName changes
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    private void updateName() {
        this.name = this.firstName + " " + this.lastName;
    }
}
