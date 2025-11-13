package com.tradeshift.controller;

import com.tradeshift.service.MarketDataService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "http://localhost:5173") // adjust as per frontend
public class MarketController {

    private final MarketDataService marketService;

    public MarketController(MarketDataService marketService) {
        this.marketService = marketService;
    }

    // Get live price by symbol
    @GetMapping("/price/{symbol}")
    public BigDecimal getPrice(@PathVariable String symbol) {
        return marketService.getPrice(symbol.toUpperCase());
    }

    // Search for company/symbol by keyword
    @GetMapping("/search")
    public Object searchStocks(@RequestParam String query) {
        return marketService.searchStocks(query);
    }
}
