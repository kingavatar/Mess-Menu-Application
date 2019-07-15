package com.kingavatar.menuapp;


public class Menu_items {
    private String items;
    private Float rating;
    private String description;

    Menu_items() {

    }

    Menu_items(String items) {
        this.items = items;
        this.description = "";
        this.rating = 0.0f;
    }

    Menu_items(String items, String description) {
        this.items = items;
        this.description = description;
        this.rating = 0.0f;
    }
    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}