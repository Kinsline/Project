package com.tradeshift.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;  

    @JsonProperty("type") 
    private String orderType; 

    private Double quantity;
    private Double price;
    private String status; // e.g., PENDING, COMPLETED, CANCELLED
    private LocalDateTime orderTime;

    // ✅ Optional — Link order to a user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Order() {}

    // ✅ Automatically set time and default status before saving
    @PrePersist
    public void onCreate() {
        this.orderTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
