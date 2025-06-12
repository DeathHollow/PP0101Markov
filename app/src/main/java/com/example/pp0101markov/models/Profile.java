package com.example.pp0101markov.models;

public class Profile {
    private String id;
    private String name;
    private String avatar_url;
    private String email;

    public String getId() {
        return id;
    }

    public String getName() { return name; }

    public void setId(String id) {
        this.id = id;
    }



    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setName(String name) { this.name = name; }
    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
