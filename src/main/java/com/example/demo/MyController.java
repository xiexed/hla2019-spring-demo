package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class MyController {

    List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    WebSocketConfigurer webSocketConfigurer() {
        return new WebSocketConfigurer() {
            @Override
            public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
                registry.addHandler(new TextWebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        webSocketSessions.add(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                        webSocketSessions.remove(session);
                    }

                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                        String payload = message.getPayload();
                        Product product = objectMapper.readValue(payload, Product.class);
                        addProduct(product);
                    }
                }, "/ws");
            }
        };
    }


    List<SseEmitter> sseEmitters = Collections.synchronizedList(new ArrayList<>());

    @GetMapping("/events")
    public Object hello() {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> {
            sseEmitters.remove(sseEmitter);
        });
        return sseEmitter;
    }

    @GetMapping("/listProducts")
    public List<Product> listProducts() {
        return products;
    }


    final HazelcastInstance hazelcastInstance;

    List<Product> products;

    public MyController(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.products = hazelcastInstance.getList("products111");
        hazelcastInstance.<Product>getTopic("productAdd").addMessageListener(message -> {
            for (WebSocketSession webSocketSession : webSocketSessions) {
                try {
                    webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(products)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addProduct(Product product) {
        products.add(product);
        hazelcastInstance.<Product>getTopic("productAdd").publish(product);
    }

    @PostMapping("/postForm")
    public List<Product> processForm(@RequestBody Product product) throws IOException {
//        System.out.println("name =" + name + " price = " + price + " model = " + model.asMap());
        System.out.println("product =" + product);
        addProduct(product);
        for (SseEmitter sseEmitter : sseEmitters) {
            sseEmitter.send("update");
        }
        return products;
    }

}

class Product implements Serializable {

    private String name;
    private int price;
    private boolean inStock;

    public Product(String name, int price, boolean inStock) {
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", inStock=" + inStock +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
