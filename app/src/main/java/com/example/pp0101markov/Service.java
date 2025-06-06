package com.example.pp0101markov;

public class Service {
    private int imageResId;
    private String title;
    private String price;

    public Service(int imageResId, String title, String price) {
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

    public String getPrice() {
        return price;
    }
}
