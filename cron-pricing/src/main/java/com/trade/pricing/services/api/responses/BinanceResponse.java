package com.trade.pricing.services.api.responses;


import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonDeserializer;
import tools.jackson.databind.atabind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonDeserialize(using = BinanceResponse.BinanceResponseDeserializer.class)
public class BinanceResponse {
    private List<BinanceSingleResponse> symbolResponse;

    public static class BinanceResponseDeserializer extends JsonDeserializer<BinanceResponse> {
        @Override
        public BinanceResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            List<BinanceSingleResponse> list = p.readValueAs(new TypeReference<List<BinanceSingleResponse>>() {});
            return new BinanceResponse(list);
        }
    }
}
