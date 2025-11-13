package com.tradeshift.controller;

import com.tradeshift.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/portfolio")

	public class PortfolioController {
	
	private final PortfolioService portfolioService;
	
	public PortfolioController(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
}

@GetMapping
	public ResponseEntity<?> get(@AuthenticationPrincipal UserDetails ud){
		UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // replace with real mapping
	// In production: lookup user by email to get UUID
			return ResponseEntity.ok(portfolioService.getTotal(userId));
		}
}