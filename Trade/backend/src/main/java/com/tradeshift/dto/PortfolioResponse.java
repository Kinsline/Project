package com.tradeshift.dto;

import java.math.BigDecimal;
import java.util.List;

	public class PortfolioResponse {
	private BigDecimal totalValue;
	private List<?> holdings; 
	
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public List<?> getHoldings() {
		return holdings;
	}
	public void setHoldings(List<?> holdings) {
		this.holdings = holdings;
	}
	}