package com.trade.core.controller;

import com.trade.core.dto.TradeRequest;
import com.trade.core.dto.TradeResponse;
import com.trade.core.service.TradingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    
    private final TradingService tradingService;

    public TradeController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping("/execute")
    public ResponseEntity<TradeResponse> executeTrade(@RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradingService.executeTrade(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Page<TradeResponse>> getTradeHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tradingService.getTradeHistory(userId, PageRequest.of(page, size)));
    }
}
