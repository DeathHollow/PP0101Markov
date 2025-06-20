package com.example.pp0101markov.models;

public class Profile {
    private String id;
    private String full_name;
    private String avatar_url;
    private String email;

    public String getId() {
        return id;
    }

    public String getName() { return full_name; }

    public void setId(String id) {
        this.id = id;
    }



    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setName(String name) { this.full_name = name; }
    public String getAvatar_url() {
        return "https://cicljuulqucdsfkqygib.supabase.co/storage/v1/object/avatars/" + avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
