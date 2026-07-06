/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.model;

import java.math.BigDecimal;

/**
 *
 * @author ADMIN
 */
public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private int flowerID;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailID, int orderID,
            int flowerID, int quantity, BigDecimal unitPrice) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.flowerID = flowerID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
