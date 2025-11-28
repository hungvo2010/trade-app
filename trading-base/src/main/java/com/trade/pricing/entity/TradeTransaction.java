package com.trade.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_transaction", indexes = {
    @Index(name = "idx_user_executed", columnList = "user_id, executed_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 10)
    private String side;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(name = "executed_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal executedPrice;

    @Column(name = "total_cost", nullable = false, precision = 20, scale = 8)
    private BigDecimal totalCost;

    @Column(name = "fee_amount", nullable = false, precision = 20, scale = 8)
    private BigDecimal feeAmount;

    @Column(name = "net_amount", nullable = false, precision = 20, scale = 8)
    private BigDecimal netAmount;

    @Column(name = "executed_at", nullable = false, updatable = false)
    private LocalDateTime executedAt;

    @Column(name = "aggregated_from", length = 10)
    private String aggregatedFrom;

    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
        if (feeAmount == null) feeAmount = BigDecimal.ZERO;
    }
}
