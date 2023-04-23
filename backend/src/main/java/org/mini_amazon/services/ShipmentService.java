package org.mini_amazon.services;

import org.hibernate.sql.ast.tree.expression.Collation;
import org.mini_amazon.controllers.OrderController;
import org.mini_amazon.enums.ShipmentStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.repositories.ShipmentRepository;
import org.mini_amazon.socket_servers.AmazonDaemon;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Resource;

@Service
public class ShipmentService {

  @Resource
  private ShipmentRepository shipmentRepository;
  @Resource
  private OrderService orderService;
  @Resource
  private WarehouseService warehouseService;
  @Resource
  private ItemService itemService;


  @Transactional(readOnly = true)
  public Shipment getShipmentById(String id) throws ServiceError {
    Optional<Shipment> shipment = shipmentRepository.findById(id);
    if (shipment.isEmpty()) {
      throw new ServiceError("Shipment does not exist.");
    } else {
      return shipment.get();
    }
  }


  @Transactional
  // long itemId, double quantity
  public Shipment createShipment(List<OrderController.OrderRequest> orderRequests, int destinationX, int destinationY) throws ServiceError {
    if (orderRequests == null || orderRequests.isEmpty()) {
      throw new ServiceError("No order pairs provided");
    }
    Warehouse warehouse = warehouseService.getWarehouseByDestination(destinationX, destinationY);
    List<Order> orders = new ArrayList<>();
    double totalPrice = 0;
    for (OrderController.OrderRequest orderPair : orderRequests) {
      Order order = orderService.createOrder(orderPair.itemId(), orderPair.quantity());

      orders.add(order);
      totalPrice += order.getItem().getUnitPrice() * order.getQuantity();
    }
    Shipment newShipment = new Shipment();
    newShipment.setOrders(orders);
    newShipment.setDestinationX(destinationX);
    newShipment.setDestinationY(destinationY);
    newShipment.setTotalPrice(totalPrice);
    newShipment.setWarehouse(warehouse);
    newShipment.setOrders(orders);
    Shipment shipment = shipmentRepository.save(newShipment);
    for (Order order : orders) {
      order.setShipment(shipment);
//      orderRepository.save(order);
//      orderService.updateOrder(order.getId(), order);
    }
    return shipment;
  }


  @Transactional(readOnly = true)
  public Shipment getPendingShipmentBySameOrder(WorldAmazonProtocol.APurchaseMore aPurchaseMore) throws ServiceError {

    List<Shipment> shipments = shipmentRepository.findShipmentsByStatusAndWarehouseId(ShipmentStatus.PENDING, aPurchaseMore.getWhnum());
    System.out.println("all shipments: " + shipmentRepository.findAll());
    System.out.println("shipments: " + shipments);
    if (shipments.isEmpty()) {
      // never reach here
      return null;
    }
    List<WorldAmazonProtocol.AProduct> aProducts = aPurchaseMore.getThingsList();
    List<Order> orders = new ArrayList<>();
    for (WorldAmazonProtocol.AProduct p : aProducts) {
      Item item = itemService.getItemById(p.getId());
      Order order = new Order();
      order.setId(p.getId());
      order.setQuantity(p.getCount());
      order.setItem(item);
      orders.add(order);
    }
    orders.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
    for (Shipment shipment : shipments) {
      List<Order> shipmentOrders = shipment.getOrders();
      shipmentOrders.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
      System.out.println("shipmentOrders: " + shipmentOrders);
      System.out.println("orders: " + orders);
      if (shipmentOrders.equals(orders)) {
        return shipment;
      }
    }
    throw new ServiceError("No such shipment");
  }


}

