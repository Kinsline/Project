package com.tradeshift.controller;

import com.tradeshift.entities.Order;
import com.tradeshift.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173") // or 5174 if that’s your port
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Create a new order
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // ✅ Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // ✅ Get order by ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // ✅ Get today's trade summary (for Dashboard)
    @GetMapping("/today")
    public Map<String, Object> getTodayTradeSummary() {
        List<Order> todayOrders = orderService.getAllOrders().stream()
                .filter(o -> o.getOrderTime() != null &&
                             o.getOrderTime().toLocalDate().equals(LocalDate.now()))
                .toList();

        long buyCount = todayOrders.stream()
                .filter(o -> "BUY".equalsIgnoreCase(o.getOrderType()))
                .count();

        long sellCount = todayOrders.stream()
                .filter(o -> "SELL".equalsIgnoreCase(o.getOrderType()))
                .count();

        return Map.of(
                "totalTrades", todayOrders.size(),
                "buyTrades", buyCount,
                "sellTrades", sellCount
        );
    }
}
