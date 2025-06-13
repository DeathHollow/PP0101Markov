package com.example.pp0101markov.models;

public class Service_main {
    private String name;
    private String duration;
    private String price;
    private int imageResId;

    public Service_main(String name, String duration, String price, int imageResId) {
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
