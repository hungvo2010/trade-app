package com.trade.pricing.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BinanceSingleResponse {
    private String symbol;
    private BigDecimal bidPrice;
    @JsonProperty("bidQty")
    private BigDecimal bidQuantity;
    private BigDecimal askPrice;
    @JsonProperty("askQty")
    private BigDecimal askQuantity;
}
