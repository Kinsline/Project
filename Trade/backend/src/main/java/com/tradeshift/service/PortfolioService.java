package com.tradeshift.service;

import org.springframework.stereotype.Service;
import com.tradeshift.Repositories.HoldingRepository;
import java.math.BigDecimal;
import java.util.UUID;

@Service

public class PortfolioService {

    private final HoldingRepository holdingRepo;
    private final MarketDataService market;
    
    public PortfolioService(HoldingRepository holdingRepo,MarketDataService market) {
    	this.holdingRepo =holdingRepo;
    	this.market = market;
    	}
    

    public BigDecimal getTotal(UUID userId) {
        return holdingRepo.findByUserId(userId)
                .stream()
                .map(h -> market.getPrice(h.getSymbol()).multiply(h.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
