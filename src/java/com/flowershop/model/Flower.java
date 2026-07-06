/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.model;

/**
 *
 * @author ADMIN
 */
public class Flower {

    private int flowerId;
    private String flowerName;
    private double price;
    private int quantity;
    private String description;
    private String image;
    private int categoryId;
    private boolean status;

    public Flower() {
    }

    public Flower(int flowerId, String flowerName, double price,
                  int quantity, String description, String image,
                  int categoryId, boolean status) {
        this.flowerId = flowerId;
        this.flowerName = flowerName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.status = status;
    }

    public int getFlowerId() {
        return flowerId;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setFlowerId(int flowerId) {
        this.flowerId = flowerId;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Flower{" + "flowerId=" + flowerId + ", flowerName=" + flowerName + ", price=" + price + ", quantity=" + quantity + ", description=" + description + ", image=" + image + ", categoryId=" + categoryId + ", status=" + status + '}';
    }
    
}