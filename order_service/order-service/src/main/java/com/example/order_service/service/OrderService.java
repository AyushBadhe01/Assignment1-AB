package com.example.order_service.service;

import com.example.order_service.model.Order;
import com.example.order_service.model.Product;
import com.example.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public OrderService(OrderRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Order createOrder(Order order) {

        String url = productServiceUrl + "/" + order.getProductId();

        ResponseEntity<Product> response =
                restTemplate.getForEntity(url, Product.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

            Product product = response.getBody();

            if (product.getQuantity() >= order.getQuantity()) {
                order.setTotalPrice(product.getPrice() * order.getQuantity());
                order.setStatus("CONFIRMED");
                return repository.save(order);
            }
        }

        order.setStatus("FAILED");
        return repository.save(order);
    }
}
