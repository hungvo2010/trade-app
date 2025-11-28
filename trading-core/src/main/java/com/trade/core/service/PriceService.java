package com.trade.core.service;

import com.trade.core.dto.LatestPriceResponse;
import com.trade.core.repository.PriceSnapshotRepository;
import com.trade.pricing.entity.PriceSnapshot;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    private final PriceSnapshotRepository priceSnapshotRepository;

    public PriceService(PriceSnapshotRepository priceSnapshotRepository) {
        this.priceSnapshotRepository = priceSnapshotRepository;
    }

    public LatestPriceResponse getLatestPrice(String symbol) {
        PriceSnapshot snapshot = priceSnapshotRepository
                .findTopBySymbolOrderByCapturedAtDesc(symbol.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No price data found for symbol: " + symbol));

        return mapToResponse(snapshot);
    }

    private LatestPriceResponse mapToResponse(PriceSnapshot snapshot) {
        return new LatestPriceResponse(
                snapshot.getSymbol(),
                snapshot.getBidPrice(),
                snapshot.getAskPrice(),
                snapshot.getBidExchange(),
                snapshot.getAskExchange(),
                snapshot.getCapturedAt()
        );
    }
}
