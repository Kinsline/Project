package com.tradeshift.service;

import com.tradeshift.Repositories.HoldingRepository;
import com.tradeshift.Repositories.OrderRepository;
import com.tradeshift.Repositories.UserRepository;
import com.tradeshift.dto.OrderRequest;
import com.tradeshift.entities.Holding;
import com.tradeshift.entities.Order;
import com.tradeshift.entities.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TradeService {

    private final OrderRepository orderRepo;
    private final HoldingRepository holdingRepo;
    private final MarketDataService market;
    private final UserRepository userRepo;

    public TradeService(OrderRepository orderRepo, 
                        HoldingRepository holdingRepo, 
                        MarketDataService market,
                        UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.holdingRepo = holdingRepo;
        this.market = market;
        this.userRepo = userRepo;
    }

    public Order execute(OrderRequest req, UUID userId) {
        BigDecimal price = market.getPrice(req.getSymbol());

        // ✅ Fetch user entity
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Create new order
        Order order = new Order();
        order.setUser(user);
        order.setSymbol(req.getSymbol());
        order.setOrderType(req.getSide()); // matches entity field name
        order.setQuantity(req.getQuantity().doubleValue());
        order.setPrice(price.doubleValue());
        order.setStatus("EXECUTED");
        order.setOrderTime(LocalDateTime.now());
        orderRepo.save(order);

        // ✅ Update holdings
        Holding holding = holdingRepo.findByUserIdAndSymbol(userId, req.getSymbol())
                .orElse(new Holding());

        holding.setUserId(user);
        holding.setSymbol(req.getSymbol());

        BigDecimal currentQty = holding.getQuantity() != null ? holding.getQuantity() : BigDecimal.ZERO;
        BigDecimal newQty;

        if ("BUY".equalsIgnoreCase(req.getSide())) {
            newQty = currentQty.add(req.getQuantity());
        } else if ("SELL".equalsIgnoreCase(req.getSide())) {
            newQty = currentQty.subtract(req.getQuantity());
        } else {
            throw new IllegalArgumentException("Invalid order side: " + req.getSide());
        }

        holding.setQuantity(newQty);
        holding.setAvgPrice(price);
        holdingRepo.save(holding);

        return order;
    }
}
