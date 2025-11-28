package com.trade.core.service;

import com.trade.core.dto.TradeRequest;
import com.trade.core.dto.TradeResponse;
import com.trade.core.exception.InsufficientBalanceException;
import com.trade.core.exception.InvalidTradeException;
import com.trade.core.exception.PriceNotFoundException;
import com.trade.core.repository.PriceSnapshotRepository;
import com.trade.core.repository.TradeTransactionRepository;
import com.trade.pricing.entity.PriceSnapshot;
import com.trade.pricing.entity.TradeTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TradingService {
    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);
    
    private final TradeTransactionRepository tradeTransactionRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final WalletService walletService;

    public TradingService(TradeTransactionRepository tradeTransactionRepository,
                         PriceSnapshotRepository priceSnapshotRepository,
                         WalletService walletService) {
        this.tradeTransactionRepository = tradeTransactionRepository;
        this.priceSnapshotRepository = priceSnapshotRepository;
        this.walletService = walletService;
    }

    @Transactional
    public TradeResponse executeTrade(TradeRequest request) {
        logger.info("Executing trade: {}", request);
        
        PriceSnapshot latestPrice = priceSnapshotRepository
            .findTopBySymbolOrderByCapturedAtDesc(request.getSymbol().toLowerCase())
            .orElseThrow(() -> new PriceNotFoundException("No price data available for " + request.getSymbol()));

        BigDecimal executedPrice;
        String exchange;
        String baseCurrency = extractBaseCurrency(request.getSymbol());
        String quoteCurrency = extractQuoteCurrency(request.getSymbol());

        if ("BUY".equalsIgnoreCase(request.getSide())) {
            executedPrice = latestPrice.getAskPrice();
            exchange = latestPrice.getAskExchange();
            
            BigDecimal totalCost = executedPrice.multiply(request.getQuantity());
            
            BigDecimal quoteBalance = walletService.getBalance(request.getUserId(), quoteCurrency);
            if (quoteBalance.compareTo(totalCost) < 0) {
                throw new InsufficientBalanceException(
                    String.format("Insufficient %s balance. Required: %s, Available: %s", 
                        quoteCurrency, totalCost, quoteBalance));
            }
            
            walletService.updateBalance(request.getUserId(), quoteCurrency, totalCost.negate());
            walletService.updateBalance(request.getUserId(), baseCurrency, request.getQuantity());
            
            return saveTradeTransaction(request, executedPrice, totalCost, exchange);
            
        } else if ("SELL".equalsIgnoreCase(request.getSide())) {
            executedPrice = latestPrice.getBidPrice();
            exchange = latestPrice.getBidExchange();
            
            BigDecimal baseBalance = walletService.getBalance(request.getUserId(), baseCurrency);
            if (baseBalance.compareTo(request.getQuantity()) < 0) {
                throw new InsufficientBalanceException(
                    String.format("Insufficient %s balance. Required: %s, Available: %s", 
                        baseCurrency, request.getQuantity(), baseBalance));
            }
            
            BigDecimal totalCost = executedPrice.multiply(request.getQuantity());
            
            walletService.updateBalance(request.getUserId(), baseCurrency, request.getQuantity().negate());
            walletService.updateBalance(request.getUserId(), quoteCurrency, totalCost);
            
            return saveTradeTransaction(request, executedPrice, totalCost, exchange);
            
        } else {
            throw new InvalidTradeException("Invalid trade side: " + request.getSide() + ". Must be BUY or SELL");
        }
    }

    private TradeResponse saveTradeTransaction(TradeRequest request, BigDecimal executedPrice,
                                                  BigDecimal totalCost, String exchange) {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUserId(request.getUserId());
        transaction.setSymbol(request.getSymbol());
        transaction.setSide(request.getSide().toUpperCase());
        transaction.setQuantity(request.getQuantity());
        transaction.setExecutedPrice(executedPrice);
        transaction.setTotalCost(totalCost);
        transaction.setFeeAmount(BigDecimal.ZERO);
        transaction.setNetAmount(totalCost);
        transaction.setAggregatedFrom(exchange);
        
        TradeTransaction saved = tradeTransactionRepository.save(transaction);
        logger.info("Trade executed successfully: {}", saved.getTradeId());
        
        return mapToResponse(saved);
    }

    public Page<TradeResponse> getTradeHistory(Long userId, Pageable pageable) {
        return tradeTransactionRepository
            .findByUserIdOrderByExecutedAtDesc(userId, pageable)
            .map(this::mapToResponse);
    }

    private TradeResponse mapToResponse(TradeTransaction transaction) {
        return new TradeResponse(
            transaction.getTradeId(),
            transaction.getSymbol(),
            transaction.getSide(),
            transaction.getQuantity(),
            transaction.getExecutedPrice(),
            transaction.getTotalCost(),
            transaction.getFeeAmount(),
            transaction.getNetAmount(),
            transaction.getAggregatedFrom(),
            transaction.getExecutedAt()
        );
    }

    private String extractBaseCurrency(String symbol) {
        if (symbol.toUpperCase().endsWith("USDT")) {
            return symbol.substring(0, symbol.length() - 4).toUpperCase();
        }
        return symbol.substring(0, 3).toUpperCase();
    }

    private String extractQuoteCurrency(String symbol) {
        if (symbol.toUpperCase().endsWith("USDT")) {
            return "USDT";
        }
        return symbol.substring(3).toUpperCase();
    }
}
