package com.example.pp0101markov.models;

public class Service {
    private int imageResId;
    private String title;
    private double price;

    public Service(int imageResId, String title, double price) {
        this.imageResId = imageResId;
        this.title = title;
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }
}
