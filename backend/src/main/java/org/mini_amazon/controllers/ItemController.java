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
import org.mini_amazon.repositories.WarehouseRepository;
import org.mini_amazon.services.ItemService;
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

//@SpringBootApplication
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ItemController {

  //  @Resource
//  private ItemRepository itemRepository;
//
//  @Resource
//  private OrderRepository orderRepository;
//
//  @Resource
//  private ShipmentRepository shipmentRepository;
//  @Resource
//  private WarehouseRepository warehouseRepository;
  @Resource
  private ItemService itemService;

//  @GetMapping("/api")
//  public ResponseEntity<List<Item>> getItems() {
//    List<Item> items = itemRepository.findAll();
//    return ResponseEntity.ok().body(items);
//  }

  @GetMapping("/items")
  public ResponseEntity<Page<Item>> getItems(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(defaultValue = "") String search,
                                             @RequestParam(defaultValue = "id") String... sortBy) {
//    warehouseRepository.findAll();
    Page<Item> items = itemService.listOnSaleItems(page, size, search, sortBy);
//    items.
    return ResponseEntity.ok().body(items);
  }


}
