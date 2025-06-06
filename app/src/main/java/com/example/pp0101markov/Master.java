package com.example.pp0101markov;

public class Master {
    private int imageResId;
    private String name;
    private String specialization;
    private double rating;

    public Master(int imageResId, String name, String specialization, double rating) {
        this.imageResId = imageResId;
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public double getRating() {
        return rating;
    }
}
