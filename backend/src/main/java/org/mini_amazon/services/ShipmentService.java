package org.mini_amazon.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mini_amazon.controllers.OrderController;
import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.enums.ShipmentStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.models.User;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.ShipmentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Resource
  private UserService userService;
  @Resource
  private EmailService emailService;

  @Transactional(readOnly = true)
  public Shipment getShipmentById(long id) throws ServiceError {
    Optional<Shipment> shipment = shipmentRepository.findById(id);
    if (shipment.isEmpty()) {
      throw new ServiceError("Shipment does not exist.");
    } else {
      return shipment.get();
    }
  }

  @Resource
  private OrderRepository orderRepository;

  @Transactional
  // long itemId, double quantity
  public Shipment createShipment(List<OrderController.OrderRequest> orderRequests, int destinationX, int destinationY, String upsName)
          throws ServiceError {
    User user = userService.getCurrentUser();
    if (orderRequests == null || orderRequests.isEmpty()) {
      throw new ServiceError("No order pairs provided");
    }
    Warehouse warehouse = warehouseService.getWarehouseByDestination(destinationX, destinationY);
    double totalPrice = 0;
    Shipment newShipment = new Shipment();
    for (OrderController.OrderRequest orderPair : orderRequests) {
      // *******
      // Order order = orderService.createOrder(orderPair.itemId(),
      // orderPair.quantity());
      // ********
      Order order = orderService.findOrderById(orderPair.id());
      order.setStatus(OrderStatus.PROCESSING);
      // Order order = new Order();
      // order.setItem(itemService.getItemById(orderPair.itemId()));
      // order.setQuantity(orderPair.quantity());
      // orders.add(order);
      // order
      // Order order =
      totalPrice += order.getItem().getUnitPrice() * order.getQuantity();

      newShipment.addOrder(order);

    }

    newShipment.setDestinationX(destinationX);
    newShipment.setDestinationY(destinationY);
    newShipment.setTotalPrice(totalPrice);
    newShipment.setWarehouse(warehouse);
    // newShipment.setOrders(orders);
    newShipment.setOwner(user);
    newShipment.setUpsName(upsName);
    Shipment shipment = shipmentRepository.save(newShipment);
    for (OrderController.OrderRequest orderPair : orderRequests) {
      Order order = orderService.findOrderById(orderPair.id());
      order.setShipment(shipment);
      orderRepository.save(order);
    }
    emailService.sendEmail(user.getEmail(), "shipment status", "shipment created. ");
    return shipment;
  }

  @Transactional
  public Shipment updateShipmentStatus(long id, ShipmentStatus status) throws ServiceError {
//    User user = userService.getCurrentUser();
    Shipment shipment = getShipmentById(id);
    User user = shipment.getOwner();
    if (shipment.canUpdatedTo(status)) {
      shipment.setStatus(status);
      emailService.sendEmail(user.getEmail(), "shipment status", status.name());
      if (status == ShipmentStatus.DELIVERED) {
        for (Long orderId : shipment.getOrders().stream().map(Order::getId).toList()) {
          orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
        }
      }
      return shipmentRepository.save(shipment);
    } else {
      throw new ServiceError(
              "Cannot update shipment status from " + shipment.getStatus() + " to " + status);
    }
  }

  @Transactional
  public Shipment updateShipmentTruckId(long id, Integer truckId) throws ServiceError {
    Shipment shipment = getShipmentById(id);
    shipment.setTruckId(truckId);
    return shipmentRepository.save(shipment);
  }

  @Transactional
  public List<Order> getOrdersByShipmentIdAndUserId(long shipmentId) throws ServiceError {
    User user = userService.getCurrentUser();
    Shipment shipment = shipmentRepository.getShipmentByIdAndOwnerUsername(shipmentId, user.getUsername());
    return shipment.getOrders();

  }

  @Transactional(readOnly = true)
  public Shipment getPendingShipmentBySameOrder(WorldAmazonProtocol.APurchaseMore aPurchaseMore) throws ServiceError {

    List<Shipment> shipments = shipmentRepository.findShipmentsByStatusAndWarehouseId(ShipmentStatus.PENDING,
            aPurchaseMore.getWhnum());
    // System.out.println("all shipments: " + shipmentRepository.findAll());
    // System.out.println("shipments: " + shipments);
    if (shipments.isEmpty()) {
      // never reach here
      throw new ServiceError("No such shipment");
    }
    List<WorldAmazonProtocol.AProduct> aProducts = aPurchaseMore.getThingsList();
    List<Order> orders = new ArrayList<>();
    for (WorldAmazonProtocol.AProduct p : aProducts) {
      Item item = itemService.getItemById(p.getId());
      Order order = new Order();
      // order.setId(p.getId());
      order.setQuantity(p.getCount());
      order.setItem(item);
      orders.add(order);
    }
    return findShipmentMatchByOrders(shipments, orders);
  }

  private Shipment findShipmentMatchByOrders(List<Shipment> shipments, List<Order> orders) throws ServiceError {
    orders.sort((o1, o2) -> (int) (o1.getItem().getId() - o2.getItem().getId()));
    for (Shipment shipment : shipments) {
      if (shipment.getOrders().size() != orders.size()) {
        continue;
      }
      List<Order> shipmentOrders = shipment.getOrders();
      shipmentOrders.sort((o1, o2) -> (int) (o1.getItem().getId() - o2.getItem().getId()));
      // System.out.println("shipmentOrders: " + shipmentOrders);
      // System.out.println("orders: " + orders);
      if (ifShipmentOrdersEqualOrders(shipmentOrders, orders)) {
        return shipment;
      }
    }
    throw new ServiceError("No such shipment");
  }

  // two list are sorted
  private boolean ifShipmentOrdersEqualOrders(List<Order> shipmentOrders, List<Order> orders) {
    for (int i = 0; i < shipmentOrders.size(); i++) {
      if (!ifTwoOrderContainsSameItem(shipmentOrders.get(i), orders.get(i))) {
        return false;
      }
    }
    return true;
  }

  private boolean ifTwoOrderContainsSameItem(Order o1, Order o2) {
    return o1.getItem().getId() == o2.getItem().getId() && o1.getQuantity() == o2.getQuantity()
           && o1.getItem().getDescription().equals(o2.getItem().getDescription());
  }

  @Transactional
  public void updateShipmentAddress(long orderId, int x, int y) throws ServiceError {
    Order order = orderService.findOrderById(orderId);
    Shipment shipment = getShipmentById(order.getShipment().getId());
    shipment.setDestinationX(x);
    shipment.setDestinationY(y);
    shipmentRepository.save(shipment);
  }

}
