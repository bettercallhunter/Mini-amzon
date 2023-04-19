package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
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

  @GetMapping("/api/orders")
  public ResponseEntity<List<Order>> getOrders() {
    List<Order> orders = orderRepository.findAll();
    return ResponseEntity.ok().body(orders);
  }

  record orderRequest(String address, int quantity) {}

  @PostMapping("/api/buy/{id}")
  public ResponseEntity<Order> BuyItem(
    @PathVariable int id,
    @RequestBody orderRequest request
  ) {
    Item item = itemRepository.findById(id);

    Order new_order = new Order();
    Order last_order = orderRepository.findFirstByOrderByIdDesc();
    if (last_order == null) {
      new_order.setId(0);
    } else {
      long last_id = last_order.getId();
      new_order.setId(last_id + 1);
    }
    new_order.setItem(item);
    new_order.setQuantity(request.quantity());
    new_order.setAddress(request.address());
    OrderStatus status = OrderStatus.PROCESSING;
    new_order.setStatus(status);

    //find shipment
    Shipment shipment = shipmentRepository.findByAddress(request.address());
    if (shipment == null) {
      shipment = new Shipment();
      shipment.setAddress(request.address());

      //set shipment id
      Shipment lastShipment = shipmentRepository.findFirstByOrderByIdDesc();
      long last_id = -1;
      if (lastShipment != null) {
        last_id = lastShipment.getId();
      }
      long new_id = last_id + 1;

      shipment.setId(new_id);
    }
    shipment.addOrder(new_order);
    shipmentRepository.save(shipment);

    // orderRepository.save(new_order);
    return ResponseEntity.ok().body(new_order);
  }
}
