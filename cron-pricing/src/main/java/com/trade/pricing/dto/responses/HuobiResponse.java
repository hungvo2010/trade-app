package com.trade.pricing.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HuobiResponse {
    private List<HuobiSingleResponse> data;
}
