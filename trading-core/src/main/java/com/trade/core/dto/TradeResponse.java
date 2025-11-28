package com.trade.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponse {
    private Long tradeId;
    private String symbol;
    private String side;
    private BigDecimal quantity;
    private BigDecimal executedPrice;
    private BigDecimal totalCost;
    private BigDecimal feeAmount;
    private BigDecimal netAmount;
    private String aggregatedFrom;
    private LocalDateTime executedAt;
}
