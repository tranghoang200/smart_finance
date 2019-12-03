package com.tranghoang.expense.Model;


import android.widget.Spinner;

public class Data {

    private double amount;
    private String type;
    private String category;
    private String id;

    public Data() {
    }

    public Data(double amount, String type, String category, String id, String date) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.id = id;
        this.date = date;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public void setCategory(String category){this.category = category;}

    public String getCategory() { return category; }
}
