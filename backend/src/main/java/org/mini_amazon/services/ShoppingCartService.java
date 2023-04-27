package org.mini_amazon.services;

import com.google.protobuf.ServiceException;

import java.util.List;

import org.mini_amazon.enums.OrderStatus;
import org.mini_amazon.errors.ServiceError;
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
    private UserService userService;

    @Resource
    private ItemService itemService;

    public List<Order> getShoppingCart() throws ServiceError{
        User parsedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = parsedUser.getUsername();
        User currentUser = userService.getUserByUsername(username);
        List<Order> orders = currentUser.getCart();
        return orders;
    }

    public Order addCart(long item_id, int quantity) throws ServiceError {
        Item item = itemService.getItemById(item_id);
//        System.out.println(item);
        User parsedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = parsedUser.getUsername();
        User currentUser = userService.getUserByUsername(username);

        Order order = new Order();
        order.setItem(item);
        order.setQuantity(quantity);

        order.setStatus(OrderStatus.SHIPPINGCART);
        currentUser.addCart(order);
        userRepository.save(currentUser);
        order.setOwner(currentUser);
        orderRepository.save(order);

        return order;
    }
}
