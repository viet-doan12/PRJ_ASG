/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.model;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class Review {
     int reviewID;
    private int orderID;
    private int flowerID;
    private int userID;
    private int rating;
    private String comment;
    private Date reviewDate;
    private boolean status;

    public Review() {
    }

    public Review(int reviewID, int orderID, int flowerID, int userID,
                  int rating, String comment, Date reviewDate, boolean status) {
        this.reviewID = reviewID;
        this.orderID = orderID;
        this.flowerID = flowerID;
        this.userID = userID;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.status = status;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getFlowerID() {
        return flowerID;
    }

    public void setFlowerID(int flowerID) {
        this.flowerID = flowerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewID=" + reviewID +
                ", orderID=" + orderID +
                ", flowerID=" + flowerID +
                ", userID=" + userID +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                ", status=" + status +
                '}';
    }
    
}
