package com.tradeshift.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
public class StreamController {

    private final List<String> symbols = List.of("AAPL", "TSLA", "GOOGL", "AMZN");
    private final Random random = new Random();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPrices() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        executor.execute(() -> {
            try {
                Map<String, Double> basePrices = new HashMap<>();
                for (String symbol : symbols) {
                    basePrices.put(symbol, 100 + random.nextDouble() * 900);
                }

                while (true) {
                    for (String symbol : symbols) {
                        double change = (random.nextDouble() - 0.5) * 10;
                        double newPrice = Math.max(1, basePrices.get(symbol) + change);
                        basePrices.put(symbol, newPrice);

                        Map<String, Object> tick = Map.of(
                                "symbol", symbol,
                                "price", Math.round(newPrice * 100.0) / 100.0,
                                "timestamp", System.currentTimeMillis()
                        );

                        emitter.send(SseEmitter.event().name("price").data(tick));
                        Thread.sleep(200);
                    }
                    Thread.sleep(800);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                emitter.complete();
            }
        });

        return emitter;
    }
}
