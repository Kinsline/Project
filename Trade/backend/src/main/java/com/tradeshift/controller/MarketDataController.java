package com.tradeshift.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.WebSocket;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/prices")
@CrossOrigin(origins = "http://localhost:5173")
public class MarketDataController {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private HttpClient httpClient;
    private WebSocket webSocket;
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    @PostConstruct
    public void init() {
        httpClient = HttpClient.newHttpClient();
        try {
            connectWebSocket();
        } catch (Exception e) {
            System.err.println("⚠️ Failed to connect to Finnhub WebSocket: " + e.getMessage());
        }
    }

    private void connectWebSocket() {
        String wsUrl = "wss://ws.finnhub.io?token=" + finnhubApiKey;

        webSocket = httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        emitters.forEach(emitter -> {
                            try {
                                emitter.send(SseEmitter.event().data(data.toString()));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        });
                        return CompletableFuture.completedFuture(null);
                    }
                }).join();

        // ✅ Subscribe to sample stock symbols
        webSocket.sendText("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}", true);
        webSocket.sendText("{\"type\":\"subscribe\",\"symbol\":\"GOOGL\"}", true);
        webSocket.sendText("{\"type\":\"subscribe\",\"symbol\":\"TSLA\"}", true);

        System.out.println("✅ Connected to Finnhub WebSocket");
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPrices() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
        if (webSocket != null) {
            webSocket.abort();
        }
        System.out.println("🧹 Cleaned up WebSocket and executor");
    }
}
