package com.kingavatar.menuapp;


public class Menu_items {
    private String items;
    private Float rating;

    Menu_items() {

    }

    Menu_items(String items) {
        this.items = items;
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
}
