package com.trade.core.controller;

import com.trade.core.dto.LatestPriceResponse;
import com.trade.core.service.PriceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
public class PriceController {
    
    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/latest")
    public ResponseEntity<LatestPriceResponse> getLatestPrice(@RequestParam String symbol) {
        return ResponseEntity.ok(priceService.getLatestPrice(symbol));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<LatestPriceResponse>> getPriceHistory(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(priceService.getPriceHistory(symbol, PageRequest.of(page, size)));
    }
}
