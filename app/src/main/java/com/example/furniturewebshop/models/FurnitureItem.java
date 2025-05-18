package com.example.furniturewebshop.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class FurnitureItem implements Serializable {
    private String id;
    private String name;
    private int price;
    private String category;
    private String imageUrl;
    private Timestamp createdAt;

    public FurnitureItem() {
    }

    public FurnitureItem(String name, int price, String category, String imageUrl, Timestamp createdAt) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
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

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
