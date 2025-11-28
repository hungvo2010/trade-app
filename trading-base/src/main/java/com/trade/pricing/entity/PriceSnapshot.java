package com.trade.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_snapshot", indexes = {
    @Index(name = "idx_symbol_captured", columnList = "symbol, captured_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(name = "bid_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal askPrice;

    @Column(name = "bid_exchange", length = 10)
    private String bidExchange;

    @Column(name = "ask_exchange", length = 10)
    private String askExchange;

    @Column(name = "captured_at", nullable = false, updatable = false)
    private LocalDateTime capturedAt;

    @PrePersist
    protected void onCreate() {
        capturedAt = LocalDateTime.now();
    }
}
