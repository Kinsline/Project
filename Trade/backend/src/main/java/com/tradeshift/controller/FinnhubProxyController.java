package com.tradeshift.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class FinnhubProxyController {

    private static final String FINNHUB_WS_URL =
            "wss://ws.finnhub.io?token=d3p30v9r01quo6o6k1bgd3p30v9r01quo6o6k1c0";

    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/api/stream")
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(FINNHUB_WS_URL).build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("✅ Connected to Finnhub WebSocket");
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}");
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"MSFT\"}");
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"TSLA\"}");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JsonNode jsonNode = mapper.readTree(text);
                    if (jsonNode.has("data")) {
                        for (JsonNode item : jsonNode.get("data")) {
                            String symbol = item.get("s").asText();
                            double price = item.get("p").asDouble();

                            // ✅ send JSON instead of string
                            ObjectNode data = mapper.createObjectNode();
                            data.put("symbol", symbol);
                            data.put("price", price);

                            emitter.send(SseEmitter.event()
                                    .name("price")
                                    .data(data.toString()));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error parsing WebSocket message: " + e.getMessage());
                }
            }



            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.err.println("❌ WebSocket failed: " + t.getMessage());
                emitter.completeWithError(t);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("⚠️ WebSocket closed: " + reason);
                emitter.complete();
            }
        };

        client.newWebSocket(request, listener);

        emitter.onCompletion(() -> {
            System.out.println("🔌 SSE connection closed by client");
            client.dispatcher().executorService().shutdown();
        });

        return emitter;
    }
}
