package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//@RequestMapping("/api")
public class ItemController {

  @Resource
  private ItemRepository itemRepository;

  @Resource
  private OrderRepository orderRepository;

  @GetMapping("/api")
  public ResponseEntity<List<Item>> getItems() {
    List<Item> items = itemRepository.findAll();
    System.out.println(items.get(0).getId());
    return ResponseEntity.ok().body(items);
  }

  record orderRequest(String address, int quantity) {}

  @PostMapping("/api/buy/{id}")
  public ResponseEntity<Item> getItemById(
    @PathVariable int id,
    @RequestBody orderRequest request
  ) {
    System.out.println(request.quantity());
    Item item = itemRepository.findById(id);
    Order new_order = new Order();
    new_order.setItem(item);
    new_order.setQuantity(request.quantity());
    new_order.setAddress(request.address());

    Order last_order = orderRepository.findFirstByOrderByIdDesc();
    long last_id =  last_order.getId();
    new_order.setId(last_id+1);
    orderRepository.save(new_order);

    return ResponseEntity.ok().body(item);
  }
}
