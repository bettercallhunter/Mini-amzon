package org.mini_amazon.services;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
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

  @Transactional(readOnly = true)
  public Page<Order> listOrders(Integer pageNo, Integer pageSize, String... sortBy) {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
    Page<Order> pagedResult = orderRepository.findAll(paging);
//    System.out.println(pagedResult);
    return pagedResult;

  }

  @Transactional
  public Order createOrder(long itemId, int quantity) throws ServiceError {
    Item item = itemService.getItemById(itemId);
    Order newOrder = new Order();
    newOrder.setItem(item);
    newOrder.setQuantity(quantity);
    newOrder.setStatus(OrderStatus.PROCESSING);
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

//  // if two order lists contains the same elements, return true, order does not matter
//  public boolean compareOrders(List<Order> orderList1, List<Order> orderList2) {
//    if (orderList1.size() != orderList2.size()) {
//      return false;
//    }
//    for (Order order1 : orderList1) {
//      boolean found = false;
//      for (Order order2 : orderList2) {
//        if (order1.getId() == order2.getId()) {
//          found = true;
//          break;
//        }
//      }
//      if (!found) {
//        return false;
//      }
//    }


}
