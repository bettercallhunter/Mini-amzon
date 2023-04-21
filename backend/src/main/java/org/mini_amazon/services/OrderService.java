package org.mini_amazon.services;

import jakarta.annotation.Resource;
import java.util.List;
import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.repositories.ItemRepository;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Resource
  private UserRepository userRepository;

  @Resource
  private ItemRepository itemRepository;

  @Resource
  private OrderRepository orderRepository;

  @Resource
  private ShipmentRepository shipmentRepository;

  public void placeOrder(int id, int quantity, String address) {
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
    new_order.setQuantity(quantity);
    new_order.setAddress(address);
    OrderStatus status = OrderStatus.PROCESSING;
    new_order.setStatus(status);

    //find shipment
    Shipment shipment = shipmentRepository.findByAddress(address);
    if (shipment == null) {
      shipment = new Shipment();
      shipment.setAddress(address);

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
  }

  public List<Order> getOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders;
  }
}
