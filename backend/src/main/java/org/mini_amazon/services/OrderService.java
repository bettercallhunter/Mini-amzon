package org.mini_amazon.services;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Resource;

@Service
public class OrderService {
  @Resource
  private OrderRepository orderRepository;
  @Resource
  private ItemService itemService;
  @Resource
  private UserService userService;

  @Transactional(readOnly = true)
  public Page<Order> listOrders(Integer pageNo, Integer pageSize, String... sortBy) throws ServiceError {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
//    Page<Order> pagedResult = orderRepository.findAllByOwnerusername(paging);
    User user = userService.getCurrentUser();
    Page<Order> pagedResult = orderRepository.findAllByOwnerUsername(paging, user.getUsername());
    // System.out.println(pagedResult);
    return pagedResult;
  }

  public Order findOrderById(long id) throws ServiceError {
    Optional<Order> orderOptional = orderRepository.findById(id);
    if (orderOptional.isEmpty()) {
      throw new ServiceError("Order does not exist.");
    } else {
      return orderOptional.get();
    }
  }

  @Transactional // long shipmentId
  public Order createOrder(long itemId, int quantity) throws ServiceError {
    User user = userService.getCurrentUser();
    Item item = itemService.getItemById(itemId);
    Order newOrder = new Order();
    newOrder.setItem(item);
    newOrder.setQuantity(quantity);
    newOrder.setStatus(OrderStatus.PROCESSING);
    newOrder.setOwner(user);
    // newOrder.setShipment(shipmentService.getShipmentById(shipmentId));
    return orderRepository.save(newOrder);
  }

  @Transactional
  public Order updateOrder(long itemId, Order newOrder) throws ServiceError {
    Optional<Order> orderOptional = orderRepository.findById(itemId);
    if (orderOptional.isEmpty()) {
      throw new ServiceError("Order does not exist.");
    } else {
      Order order = orderOptional.get();
      order.setItem(newOrder.getItem());
      order.setQuantity(newOrder.getQuantity());
      order.setStatus(newOrder.getStatus());
      order.setShipment(newOrder.getShipment());
      return orderRepository.save(order);
    }
  }

  @Transactional
  public Order updateOrderStatus(long orderId, OrderStatus status) throws ServiceError {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (orderOptional.isEmpty()) {
      throw new ServiceError("Order does not exist.");
    } else {
      Order order = orderOptional.get();
      order.setStatus(status);
      return orderRepository.save(order);
    }
  }


  // // if two order lists contains the same elements, return true, order does not
  // matter
  // public boolean compareOrders(List<Order> orderList1, List<Order> orderList2)
  // {
  // if (orderList1.size() != orderList2.size()) {
  // return false;
  // }
  // for (Order order1 : orderList1) {
  // boolean found = false;
  // for (Order order2 : orderList2) {
  // if (order1.getId() == order2.getId()) {
  // found = true;
  // break;
  // }
  // }
  // if (!found) {
  // return false;
  // }
  // }

}
