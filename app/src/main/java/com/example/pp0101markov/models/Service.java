package com.example.pp0101markov.models;

public class Service {


    private String id;
    private String avatar_url;
    private String name;
    private double price;

    public Service(String id, String avatar_url, String name, double price) {
        this.id = id;
        this.avatar_url = avatar_url;
        this.name = name;
        this.price = price;
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
    public String getAvatar_url() {
        return "https://cicljuulqucdsfkqygib.supabase.co/storage/v1/object/serviceimage/" + avatar_url;
    }
    public double getPrice() {
        return price;
    }
}
