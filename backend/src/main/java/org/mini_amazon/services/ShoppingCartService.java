package org.mini_amazon.services;

import java.util.List;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.OrderRepository;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class ShoppingCartService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private OrderRepository orderRepository;

    @Resource
    private ItemService itemService;

    public List<Order> getShoppingCart() {
        User parsed_User = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orders = parsed_User.getCart();
        return orders;
    }

    public void addCart(long item_id, int quantity) throws Exception {
        Item item = itemService.getItemById(item_id);
        User parsed_User = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = new Order();
        order.setItem(item);
        order.setQuantity(quantity);

        order.setStatus(OrderStatus.SHIPPINGCART);
        parsed_User.addCart(order);
        userRepository.save(parsed_User);
        order.setOwner(parsed_User);
        orderRepository.save(order);

        return;
    }
}
