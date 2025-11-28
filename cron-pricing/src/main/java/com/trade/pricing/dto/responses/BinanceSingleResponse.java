package com.trade.pricing.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceSingleResponse {
    private String symbol;
    private BigDecimal bidPrice;
    @JsonProperty("bidQty")
    private BigDecimal bidQuantity;
    private BigDecimal askPrice;
    @JsonProperty("askQty")
    private BigDecimal askQuantity;
}
