package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.User;
import org.mini_amazon.proto.AmazonUPSProtocol;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class OrderController {
    @Resource
    private OrderRepository orderRepository;

    @GetMapping("/api/orders")
    public ResponseEntity<List<Order>>getOrders(){

        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok().body(orders);

    }

}
