package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.models.Order;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
import org.mini_amazon.services.OrderService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {

  @Resource
  private OrderRepository orderRepository;

  @Resource
  private ItemRepository itemRepository;

  @Resource
  private ShipmentRepository shipmentRepository;

  @Resource
  private OrderService orderService;

  @GetMapping("/api/orders")
  public ResponseEntity<List<Order>> getOrders() {
    List<Order> orders = orderService.getOrders();
    return ResponseEntity.ok().body(orders);
  }

  record orderRequest(String address, int quantity) {}

  @PostMapping("/api/buy/{id}")
  public ResponseEntity<String> BuyItem(
    @PathVariable int id,
    @RequestBody orderRequest request
  ) {
    orderService.placeOrder(id, request.quantity(), request.address());
    // orderRepository.save(new_order);
    return ResponseEntity.ok().body("Order placed");
  }
}
