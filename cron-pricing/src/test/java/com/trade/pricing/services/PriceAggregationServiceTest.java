package com.trade.pricing.services;

import com.trade.pricing.entity.PriceSnapshot;
import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.repositories.PriceSnapshotRepository;
import com.trade.pricing.services.api.PricingAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PriceAggregationServiceTest {

    @Mock
    private PriceSnapshotRepository priceSnapshotRepository;

    @Mock
    private PricingAPIService binanceService;

    @Mock
    private PricingAPIService huobiService;

    @InjectMocks
    private PriceAggregationService priceAggregationService;

    @BeforeEach
    void setUp() {
        priceAggregationService = new PriceAggregationService(
            priceSnapshotRepository,
            Arrays.asList(binanceService, huobiService)
        );
    }

    @Test
    void aggregateAndSaveBestPrices_Success() {
        SymbolPrice binancePrice = createSymbolPrice("btcusdt", "95000", "95100");
        SymbolPrice huobiPrice = createSymbolPrice("btcusdt", "95050", "95080");

        when(binanceService.getExchangeName()).thenReturn("BINANCE");
        when(binanceService.getPrice(anyList())).thenReturn(Collections.singletonList(binancePrice));
        
        when(huobiService.getExchangeName()).thenReturn("HUOBI");
        when(huobiService.getPrice(anyList())).thenReturn(Collections.singletonList(huobiPrice));

        priceAggregationService.aggregateAndSaveBestPrices(Collections.singletonList("btcusdt"));

        ArgumentCaptor<PriceSnapshot> captor = ArgumentCaptor.forClass(PriceSnapshot.class);
        verify(priceSnapshotRepository).save(captor.capture());

        PriceSnapshot saved = captor.getValue();
        assertEquals("btcusdt", saved.getSymbol());
        assertEquals(new BigDecimal("95050"), saved.getBidPrice());
        assertEquals(new BigDecimal("95080"), saved.getAskPrice());
        assertEquals("HUOBI", saved.getBidExchange());
        assertEquals("HUOBI", saved.getAskExchange());
    }

    @Test
    void aggregateAndSaveBestPrices_BinanceHasBestBid_HuobiHasBestAsk() {
        SymbolPrice binancePrice = createSymbolPrice("ethusdt", "3500", "3510");
        SymbolPrice huobiPrice = createSymbolPrice("ethusdt", "3490", "3505");

        when(binanceService.getExchangeName()).thenReturn("BINANCE");
        when(binanceService.getPrice(anyList())).thenReturn(Collections.singletonList(binancePrice));
        
        when(huobiService.getExchangeName()).thenReturn("HUOBI");
        when(huobiService.getPrice(anyList())).thenReturn(Collections.singletonList(huobiPrice));

        priceAggregationService.aggregateAndSaveBestPrices(Collections.singletonList("ethusdt"));

        ArgumentCaptor<PriceSnapshot> captor = ArgumentCaptor.forClass(PriceSnapshot.class);
        verify(priceSnapshotRepository).save(captor.capture());

        PriceSnapshot saved = captor.getValue();
        assertEquals(new BigDecimal("3500"), saved.getBidPrice());
        assertEquals(new BigDecimal("3505"), saved.getAskPrice());
        assertEquals("BINANCE", saved.getBidExchange());
        assertEquals("HUOBI", saved.getAskExchange());
    }

    @Test
    void aggregateAndSaveBestPrices_OneServiceFails() {
        SymbolPrice binancePrice = createSymbolPrice("btcusdt", "95000", "95100");

        lenient().when(binanceService.getExchangeName()).thenReturn("BINANCE");
        lenient().when(binanceService.getPrice(anyList())).thenReturn(Collections.singletonList(binancePrice));
        
        lenient().when(huobiService.getExchangeName()).thenReturn("HUOBI");
        lenient().when(huobiService.getPrice(anyList())).thenThrow(new RuntimeException("API Error"));

        priceAggregationService.aggregateAndSaveBestPrices(Collections.singletonList("btcusdt"));

        ArgumentCaptor<PriceSnapshot> captor = ArgumentCaptor.forClass(PriceSnapshot.class);
        verify(priceSnapshotRepository).save(captor.capture());

        PriceSnapshot saved = captor.getValue();
        assertEquals(new BigDecimal("95000"), saved.getBidPrice());
        assertEquals(new BigDecimal("95100"), saved.getAskPrice());
        assertEquals("BINANCE", saved.getBidExchange());
        assertEquals("BINANCE", saved.getAskExchange());
    }

    @Test
    void aggregateAndSaveBestPrices_NoDataAvailable() {
        when(binanceService.getPrice(anyList())).thenReturn(Collections.emptyList());
        when(huobiService.getPrice(anyList())).thenReturn(Collections.emptyList());

        priceAggregationService.aggregateAndSaveBestPrices(Collections.singletonList("btcusdt"));

        verify(priceSnapshotRepository, never()).save(any());
    }

    @Test
    void aggregateAndSaveBestPrices_SymbolNotFound() {
        SymbolPrice wrongSymbol = createSymbolPrice("ethusdt", "3500", "3510");

        lenient().when(binanceService.getExchangeName()).thenReturn("BINANCE");
        lenient().when(binanceService.getPrice(anyList())).thenReturn(Collections.singletonList(wrongSymbol));
        
        lenient().when(huobiService.getExchangeName()).thenReturn("HUOBI");
        lenient().when(huobiService.getPrice(anyList())).thenReturn(Collections.emptyList());

        priceAggregationService.aggregateAndSaveBestPrices(Collections.singletonList("btcusdt"));

        verify(priceSnapshotRepository, never()).save(any());
    }

    @Test
    void aggregateAndSaveBestPrices_MultipleSymbols() {
        SymbolPrice btcPrice = createSymbolPrice("btcusdt", "95000", "95100");
        SymbolPrice ethPrice = createSymbolPrice("ethusdt", "3500", "3510");

        lenient().when(binanceService.getExchangeName()).thenReturn("BINANCE");
        lenient().when(binanceService.getPrice(anyList()))
            .thenReturn(Collections.singletonList(btcPrice))
            .thenReturn(Collections.singletonList(ethPrice));
        
        lenient().when(huobiService.getExchangeName()).thenReturn("HUOBI");
        lenient().when(huobiService.getPrice(anyList())).thenReturn(Collections.emptyList());

        priceAggregationService.aggregateAndSaveBestPrices(Arrays.asList("btcusdt", "ethusdt"));

        verify(priceSnapshotRepository, times(2)).save(any(PriceSnapshot.class));
    }

    private SymbolPrice createSymbolPrice(String symbol, String bidPrice, String askPrice) {
        SymbolPrice price = new SymbolPrice();
        price.setSymbol(symbol);
        price.setBidPrice(new BigDecimal(bidPrice));
        price.setAskPrice(new BigDecimal(askPrice));
        return price;
    }
}
