package com.example.pp0101markov.models;

public class Transaction {
    private int id_order;
    private int id_profile;
    private String payment_method;
    private double amount;

    public Transaction() {}

    public Transaction(int id_order, int id_profile, String payment_method, double amount) {
        this.id_order = id_order;
        this.id_profile = id_profile;
        this.payment_method = payment_method;
        this.amount = amount;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getId_profile() {
        return id_profile;
    }

    public void setId_profile(int id_profile) {
        this.id_profile = id_profile;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}