/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author ADMIN
 */
public class Flower {
    private int flowerID;
    private String flowerName;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String image;
    private int categoryID;
    private boolean status;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    public Flower() {
    }

    public Flower(int flowerID, String flowerName, String description,
            BigDecimal price, int stockQuantity, String image,
            int categoryID, boolean status,
            Timestamp createdDate, Timestamp updatedDate) {
        this.flowerID = flowerID;
        this.flowerName = flowerName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.image = image;
        this.categoryID = categoryID;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public int getFlowerID() {
        return flowerID;
    }

    public void setFlowerID(int flowerID) {
        this.flowerID = flowerID;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
}
