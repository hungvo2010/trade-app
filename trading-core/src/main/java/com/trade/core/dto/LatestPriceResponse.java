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
public class LatestPriceResponse {
    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private String bidExchange;
    private String askExchange;
    private LocalDateTime capturedAt;
}
