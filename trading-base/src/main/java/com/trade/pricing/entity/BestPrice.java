package com.trade.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "best_prices", indexes = {
    @Index(name = "idx_symbol", columnList = "symbol"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(name = "best_bid_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal bestBidPrice;

    @Column(name = "best_ask_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal bestAskPrice;

    @Column(name = "bid_source", length = 20)
    private String bidSource;

    @Column(name = "ask_source", length = 20)
    private String askSource;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
