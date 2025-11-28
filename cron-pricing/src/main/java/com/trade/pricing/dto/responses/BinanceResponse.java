package com.trade.pricing.dto.responses;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
@JsonDeserialize(using = BinanceResponse.BinanceResponseDeserializer.class)
public class BinanceResponse {
    private List<BinanceSingleResponse> symbolResponse;

    public static class BinanceResponseDeserializer extends JsonDeserializer<BinanceResponse> {
        @Override
        public BinanceResponse deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
            List<BinanceSingleResponse> list = p.readValueAs(new TypeReference<List<BinanceSingleResponse>>() {
            });
            return new BinanceResponse(list);
        }
    }
}
