package com.trade.pricing.services.api.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.trade.pricing.mapper.PriceMapper;
import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.services.api.PricingAPIService;
import com.trade.pricing.dto.responses.binance.BinanceSingleResponse;
import com.trade.pricing.exceptions.APIExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class BinanceAPIImpl implements PricingAPIService {
    private static final Logger logger = LoggerFactory.getLogger(BinanceAPIImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final String EXCHANGE_NAME = "BINANCE";

    private String baseUrl = "";
    private String apiVersion;
    private String priceEndpoint;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    public BinanceAPIImpl() {

    }

    public BinanceAPIImpl(String baseUrl, String apiVersion, String priceEndpoint) {
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.priceEndpoint = priceEndpoint;
    }


    @Override
    public List<SymbolPrice> getPrice(List<String> symbol) {
        logger.info("Fetching prices for symbols: {}", symbol);
        var httpRequest = buildPriceRequest(symbol);
        var httpResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).join();
        logger.debug("Received response with status: {}", httpResponse.statusCode());
        var priceResponse = buildPriceResponse(httpResponse.body());
        var interestedSymbols = priceResponse.stream()
                .filter(resp -> symbol.stream().anyMatch(resp.getSymbol()::equalsIgnoreCase))
                .toList();
        logger.info("Successfully fetched {} prices", interestedSymbols.size());
        return PriceMapper.INSTANCE.toMultipleSymbolPrice(interestedSymbols);
    }

    private List<BinanceSingleResponse> buildPriceResponse(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {            });
        } catch (Exception e) {
            logger.error("Error parsing Binance API response", e);
            throw new APIExceptions(e, "Error parsing Binance API response");
        }
    }

    private HttpRequest buildPriceRequest(List<String> symbol) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + apiVersion + priceEndpoint))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
    }

    @Override
    public String getExchangeName() {
        return EXCHANGE_NAME;
    }

    public static void main(String[] args) {
        PricingAPIService pricingAPIService = new BinanceAPIImpl(
                "https://api.binance.com/",
                "api/v3/",
                "ticker/bookTicker"
        );
        var symbolPrice = pricingAPIService.getPrice(List.of("BTCUSDT"));
        System.out.println(symbolPrice);
    }
}
