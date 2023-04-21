package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.models.Item;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
import org.mini_amazon.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//@RequestMapping("/api")
public class ItemController {

  @Resource
  private ItemRepository itemRepository;

  @Resource
  private OrderRepository orderRepository;

  @Resource
  private ShipmentRepository shipmentRepository;

  @Resource
  private ItemService itemService;

  @GetMapping("/api")
  public ResponseEntity<List<Item>> getItems() {
    List<Item> items = itemService.getItems();
    return ResponseEntity.ok().body(items);
  }
}
