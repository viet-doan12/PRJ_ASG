package com.flowershop.model;
 
import java.io.Serializable;
import java.math.BigDecimal;
 
/**
 * CartItem - đại diện cho 1 dòng sản phẩm trong giỏ hàng.
 * KHÔNG map với bảng nào trong DB (giỏ hàng lưu ở HttpSession).
 * Khi checkout, dữ liệu từ đây sẽ được convert sang OrderDetail để insert DB.
 */
public class CartItem implements Serializable {
 
    private int flowerId;
    private String flowerName;
    private String image;
    private BigDecimal unitPrice;   // snapshot giá tại thời điểm thêm vào giỏ
    private int quantity;
    private int stockQuantity;      // để validate không mua vượt tồn kho
 
    public CartItem() {
    }
 
    public CartItem(int flowerId, String flowerName, String image,
                     BigDecimal unitPrice, int quantity, int stockQuantity) {
        this.flowerId = flowerId;
        this.flowerName = flowerName;
        this.image = image;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.stockQuantity = stockQuantity;
    }
 
    // ===== Getters & Setters =====
 
    public int getFlowerId() {
        return flowerId;
    }
 
    public void setFlowerId(int flowerId) {
        this.flowerId = flowerId;
    }
 
    public String getFlowerName() {
        return flowerName;
    }
 
    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }
 
    public String getImage() {
        return image;
    }
 
    public void setImage(String image) {
        this.image = image;
    }
 
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
 
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
 
    public int getQuantity() {
        return quantity;
    }
 
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
 
    public int getStockQuantity() {
        return stockQuantity;
    }
 
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
 
    /**
     * Thành tiền của dòng này = unitPrice * quantity
     */
    public BigDecimal getSubtotal() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem other = (CartItem) o;
        return this.flowerId == other.flowerId;
    }
 
    @Override
    public int hashCode() {
        return Integer.hashCode(flowerId);
    }
}