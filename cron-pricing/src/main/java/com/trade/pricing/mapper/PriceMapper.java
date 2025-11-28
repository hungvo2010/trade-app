package com.trade.pricing.mapper;

import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.dto.responses.BinanceResponse;
import com.trade.pricing.dto.responses.BinanceSingleResponse;
import com.trade.pricing.dto.responses.HuobiSingleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PriceMapper {
    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    @Mapping(source = "symbol", target = "symbol")
    @Mapping(source = "bidPrice", target = "bidPrice")
    @Mapping(source = "askPrice", target = "askPrice")
    @Mapping(source = "bidQuantity", target = "bidQuantity")
    @Mapping(source = "askQuantity", target = "askQuantity")
    SymbolPrice toSymbolPrice(BinanceSingleResponse response);

    @Mapping(source = "symbol", target = "symbol")
    @Mapping(source = "bid", target = "bidPrice")
    @Mapping(source = "ask", target = "askPrice")
    @Mapping(source = "bidSize", target = "bidQuantity")
    @Mapping(source = "askSize", target = "askQuantity")
    SymbolPrice toSymbolPriceHuobi(HuobiSingleResponse response);

    @Mapping(source = "symbol", target = "symbol")
    List<SymbolPrice> toMultipleSymbolPrice(List<BinanceSingleResponse> response);

    List<SymbolPrice> toMultipleSymbolPriceHuobi(List<HuobiSingleResponse> response);

    default List<SymbolPrice> toSymbolPriceBinance(BinanceResponse response) {
        return (toMultipleSymbolPrice(response.getSymbolResponse()));
    }
}
