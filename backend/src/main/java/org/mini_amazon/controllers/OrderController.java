package org.mini_amazon.controllers;

import jakarta.annotation.Resource;

import java.util.List;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
import org.mini_amazon.repositories.WarehouseRepository;
import org.mini_amazon.services.OrderService;
// import org.mini_amazon.socket_servers.AmazonDaemon;
import org.mini_amazon.utils.AMessageBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {
  @Resource
  private OrderService orderService;

  @GetMapping("/orders")
  public ResponseEntity<Page<Order>> getOrders(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(defaultValue = "id") String... sortBy) throws ServiceError {
    // TODO: list my orders
    Page<Order> orders = orderService.listOrders(page, size, sortBy);
    return ResponseEntity.ok().body(orders);
  }

  // @PostMapping("addToCart")
  // public ResponseEntity<Order> addToCart(@RequestBody OrderRequest request) {

  // }

  public record OrderRequest(long itemId, int quantity, long id) {
  }

}
