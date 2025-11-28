package com.trade.pricing.services.api.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.pricing.dto.responses.HuobiResponse;
import com.trade.pricing.exceptions.APIExceptions;
import com.trade.pricing.mapper.PriceMapper;
import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.services.api.PricingAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class HoubiAPIImpl implements PricingAPIService {
    private static final Logger logger = LoggerFactory.getLogger(HoubiAPIImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private String baseUrl = "";
    private String priceEndpoint;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    public HoubiAPIImpl() {

    }

    public HoubiAPIImpl(String baseUrl, String priceEndpoint) {
        this.baseUrl = baseUrl;
        this.priceEndpoint = priceEndpoint;
    }

    @Override
    public List<SymbolPrice> getPrice(List<String> symbols) {
        logger.info("Fetching prices for symbols: {}", symbols);
        var httpRequest = buildPriceRequest(symbols);
        var httpResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).join();
        logger.debug("Received response with status: {}", httpResponse.statusCode());
        var priceResponse = buildPriceResponse(httpResponse.body());
        var interestedSymbols = priceResponse.getData().stream()
                .filter(resp -> symbols.stream().anyMatch(resp.getSymbol()::equalsIgnoreCase)).toList();
        logger.info("Successfully fetched {} prices", interestedSymbols.size());
        return PriceMapper.INSTANCE.toMultipleSymbolPriceHuobi(interestedSymbols);
    }

    private HuobiResponse buildPriceResponse(String body) {
        try {
            return objectMapper.readValue(body, HuobiResponse.class);
        } catch (Exception e) {
            logger.error("Error parsing Huobi API response", e);
            throw new APIExceptions(e, "Error parsing Huobi API response");
        }
    }

    private HttpRequest buildPriceRequest(List<String> symbols) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + priceEndpoint))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
    }

    public static void main(String[] args) {
        PricingAPIService pricingAPIService = new HoubiAPIImpl(
                "https://api.huobi.pro/",
                "market/tickers"
        );
        var symbolPrice = pricingAPIService.getPrice(List.of("btcusdt"));
        System.out.println(symbolPrice);
    }
}
