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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private TradeTransactionRepository tradeTransactionRepository;

    @Mock
    private PriceSnapshotRepository priceSnapshotRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TradingService tradingService;

    private PriceSnapshot mockPriceSnapshot;
    private TradeRequest buyRequest;
    private TradeRequest sellRequest;

    @BeforeEach
    void setUp() {
        mockPriceSnapshot = new PriceSnapshot();
        mockPriceSnapshot.setPriceId(1L);
        mockPriceSnapshot.setSymbol("btcusdt");
        mockPriceSnapshot.setBidPrice(new BigDecimal("95000"));
        mockPriceSnapshot.setAskPrice(new BigDecimal("95100"));
        mockPriceSnapshot.setBidExchange("BINANCE");
        mockPriceSnapshot.setAskExchange("HUOBI");
        mockPriceSnapshot.setCapturedAt(LocalDateTime.now());

        buyRequest = new TradeRequest();
        buyRequest.setUserId(1L);
        buyRequest.setSymbol("BTCUSDT");
        buyRequest.setSide("BUY");
        buyRequest.setQuantity(new BigDecimal("0.5"));

        sellRequest = new TradeRequest();
        sellRequest.setUserId(1L);
        sellRequest.setSymbol("BTCUSDT");
        sellRequest.setSide("SELL");
        sellRequest.setQuantity(new BigDecimal("0.5"));
    }

    @Test
    void executeTrade_BuySuccess() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));
        when(walletService.getBalance(1L, "USDT"))
            .thenReturn(new BigDecimal("50000"));
        
        TradeTransaction savedTransaction = createMockTransaction(1L, "BUY");
        when(tradeTransactionRepository.save(any(TradeTransaction.class)))
            .thenReturn(savedTransaction);

        TradeResponse response = tradingService.executeTrade(buyRequest);

        assertNotNull(response);
        assertEquals(1L, response.getTradeId());
        assertEquals("BUY", response.getSide());
        assertEquals(new BigDecimal("95100"), response.getExecutedPrice());
        assertEquals("HUOBI", response.getAggregatedFrom());

        verify(walletService).updateBalance(eq(1L), eq("USDT"), any(BigDecimal.class));
        verify(walletService).updateBalance(eq(1L), eq("BTC"), eq(new BigDecimal("0.5")));
        verify(tradeTransactionRepository).save(any(TradeTransaction.class));
    }

    @Test
    void executeTrade_SellSuccess() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));
        when(walletService.getBalance(1L, "BTC"))
            .thenReturn(new BigDecimal("1.0"));
        
        TradeTransaction savedTransaction = createMockTransaction(2L, "SELL");
        when(tradeTransactionRepository.save(any(TradeTransaction.class)))
            .thenReturn(savedTransaction);

        TradeResponse response = tradingService.executeTrade(sellRequest);

        assertNotNull(response);
        assertEquals(2L, response.getTradeId());
        assertEquals("SELL", response.getSide());
        assertEquals(new BigDecimal("95000"), response.getExecutedPrice());
        assertEquals("BINANCE", response.getAggregatedFrom());

        verify(walletService).updateBalance(eq(1L), eq("BTC"), any(BigDecimal.class));
        verify(walletService).updateBalance(eq(1L), eq("USDT"), any(BigDecimal.class));
    }

    @Test
    void executeTrade_PriceNotFound() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> 
            tradingService.executeTrade(buyRequest));

        verify(walletService, never()).updateBalance(anyLong(), anyString(), any());
        verify(tradeTransactionRepository, never()).save(any());
    }

    @Test
    void executeTrade_InsufficientBalanceForBuy() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));
        when(walletService.getBalance(1L, "USDT"))
            .thenReturn(new BigDecimal("1000"));

        assertThrows(InsufficientBalanceException.class, () -> 
            tradingService.executeTrade(buyRequest));

        verify(walletService, never()).updateBalance(anyLong(), anyString(), any());
        verify(tradeTransactionRepository, never()).save(any());
    }

    @Test
    void executeTrade_InsufficientBalanceForSell() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));
        when(walletService.getBalance(1L, "BTC"))
            .thenReturn(new BigDecimal("0.1"));

        assertThrows(InsufficientBalanceException.class, () -> 
            tradingService.executeTrade(sellRequest));

        verify(walletService, never()).updateBalance(anyLong(), anyString(), any());
        verify(tradeTransactionRepository, never()).save(any());
    }

    @Test
    void executeTrade_InvalidSide() {
        TradeRequest invalidRequest = new TradeRequest();
        invalidRequest.setUserId(1L);
        invalidRequest.setSymbol("BTCUSDT");
        invalidRequest.setSide("INVALID");
        invalidRequest.setQuantity(new BigDecimal("0.5"));

        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));

        assertThrows(InvalidTradeException.class, () -> 
            tradingService.executeTrade(invalidRequest));
    }

    @Test
    void executeTrade_VerifyTransactionDetails() {
        when(priceSnapshotRepository.findTopBySymbolOrderByCapturedAtDesc("btcusdt"))
            .thenReturn(Optional.of(mockPriceSnapshot));
        when(walletService.getBalance(1L, "USDT"))
            .thenReturn(new BigDecimal("50000"));
        
        TradeTransaction savedTransaction = createMockTransaction(1L, "BUY");
        when(tradeTransactionRepository.save(any(TradeTransaction.class)))
            .thenReturn(savedTransaction);

        tradingService.executeTrade(buyRequest);

        ArgumentCaptor<TradeTransaction> captor = ArgumentCaptor.forClass(TradeTransaction.class);
        verify(tradeTransactionRepository).save(captor.capture());
        
        TradeTransaction captured = captor.getValue();
        assertEquals(1L, captured.getUserId());
        assertEquals("BTCUSDT", captured.getSymbol());
        assertEquals("BUY", captured.getSide());
        assertEquals(new BigDecimal("0.5"), captured.getQuantity());
        assertEquals(BigDecimal.ZERO, captured.getFeeAmount());
    }

    private TradeTransaction createMockTransaction(Long id, String side) {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setTradeId(id);
        transaction.setUserId(1L);
        transaction.setSymbol("BTCUSDT");
        transaction.setSide(side);
        transaction.setQuantity(new BigDecimal("0.5"));
        transaction.setExecutedPrice(side.equals("BUY") ? new BigDecimal("95100") : new BigDecimal("95000"));
        transaction.setTotalCost(new BigDecimal("47550"));
        transaction.setFeeAmount(BigDecimal.ZERO);
        transaction.setNetAmount(new BigDecimal("47550"));
        transaction.setAggregatedFrom(side.equals("BUY") ? "HUOBI" : "BINANCE");
        transaction.setExecutedAt(LocalDateTime.now());
        return transaction;
    }
}
