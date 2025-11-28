package com.trade.pricing.services.api;

import com.trade.pricing.model.SymbolPrice;

import java.util.List;

public interface PricingAPIService {
    List<SymbolPrice> getPrice(List<String> symbols);
    
    String getExchangeName();
}
