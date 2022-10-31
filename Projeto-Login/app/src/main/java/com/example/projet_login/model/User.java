package com.example.projet_login.model;

public class User {
    private String id;
    private final String username;
    private final String email;
    private final String photoPath;

    public User(String username, String email, String photoPath) {
        this.username = username;
        this.email = email;
        this.photoPath = photoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
