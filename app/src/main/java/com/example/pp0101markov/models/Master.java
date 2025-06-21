package com.example.pp0101markov.models;

public class Master {


    private String id;
    private String avatar_url;
    private String name;
    private String category_id;
    private double reviews;

    public Master(String id, String avatar_url, String name, String category_id, double reviews) {
        this.id=id;
        this.avatar_url = avatar_url;
        this.name = name;
        this.category_id = category_id;
        this.reviews = reviews;
    }

    public String getAvatar_url() {
        return "https://cicljuulqucdsfkqygib.supabase.co/storage/v1/object/masterimage/" + avatar_url;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public double getReviews() {
        return reviews;
    }
}
