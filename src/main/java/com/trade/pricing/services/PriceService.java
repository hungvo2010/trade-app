package com.trade.pricing.services;

import org.springframework.stereotype.Service;

@Service
public interface PriceService {
    void getLatestPrices();
}
