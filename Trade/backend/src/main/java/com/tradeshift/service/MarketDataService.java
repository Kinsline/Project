package com.tradeshift.service;

import com.tradeshift.dto.QuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class MarketDataService {

    private final WebClient wc;

    @Value("${finnhub.api.key:}")
    private String apiKey; // inject from application.properties

    public MarketDataService() {
        this.wc = WebClient.create("https://finnhub.io/api/v1");
    }

    // ✅ Fetch current stock price safely
    public BigDecimal getPrice(String symbol) {
        try {
            QuoteResponse r = wc.get()
                    .uri(uri -> uri.path("/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(QuoteResponse.class)
                    .block();

            // ✅ Ensure r and its field 'c' are not null
            if (r != null && r.getC() != null) {
                return BigDecimal.valueOf(r.getC());
            } else {
                System.err.println("⚠️ Empty quote response for symbol: " + symbol);
                return BigDecimal.ZERO;
            }

        } catch (Exception e) {
            System.err.println("❌ Error fetching price for " + symbol + ": " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // ✅ Optional: search company by keyword
    public Object searchStocks(String query) {
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("error", "Finnhub API key missing");
        }

        try {
            return wc.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("q", query)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            System.err.println("❌ Error searching stocks: " + e.getMessage());
            return Map.of("error", "Search failed");
        }
    }
}
