package com.example.pp0101markov.models;

public class Booking {
    private int id;
    private String name;
    private String date;
    private int id_category;
    private String id_profile;
    private int id_professional;
    private int id_service;
    private int price;

    private boolean is_paid;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getId_category() { return id_category; }
    public void setId_category(int id_category) { this.id_category = id_category; }

    public String getId_profile() { return id_profile; }
    public void setId_profile(String id_profile) { this.id_profile = id_profile; }

    public int getId_professional() { return id_professional; }
    public void setId_professional(int id_professional) { this.id_professional = id_professional; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }
    public boolean isIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}