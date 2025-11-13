package com.tradeshift.dto;

import java.math.BigDecimal;

public class OrderRequest {
private String symbol;
private String side; // BUY/SELL
private BigDecimal quantity;

public String getSymbol() {
	return symbol;
}
public void setSymbol(String symbol) {
	this.symbol = symbol;
}
public String getSide() {
	return side;
}
public void setSide(String side) {
	this.side = side;
}
public BigDecimal getQuantity() {
	return quantity;
}
public void setQuantity(BigDecimal quantity) {
	this.quantity = quantity;
}
}