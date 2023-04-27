package org.mini_amazon.controllers;

import java.util.List;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Order;
import org.mini_amazon.services.AuthService;
import org.mini_amazon.services.EmailService;
import org.mini_amazon.services.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api")
public class ShippingCartController {
    @Resource
    private AuthService authService;
    @Resource
    private ShoppingCartService shoppingCartService;
    @Resource
    private EmailService emailService;

    @GetMapping("/cart")
    public ResponseEntity<List<Order>> cart() throws ServiceError {
        List<Order> cart = shoppingCartService.getShoppingCart();
        return ResponseEntity.ok().body(cart);
    }

    public record addToCartRequest(int quantity, int id) {
    }

    @PostMapping("/cart")
    public ResponseEntity<String> cart(@RequestBody addToCartRequest request) throws ServiceError {
        // System.out.println("reach cart");
        // System.out.println(request.id() + " " + request.quantity());
        if (request.quantity() <= 0) {
            throw new ServiceError("quantity must be positive");
        }

        shoppingCartService.addCart(request.id(), request.quantity());
        return ResponseEntity.ok().body("niuzie gege");

    }

    public record DeleteRequest(int order_id) {
    }

    @DeleteMapping("/cart/delete")
    public ResponseEntity<String> deleteCart(@RequestBody DeleteRequest request) throws ServiceError {
        System.out.println("reach delete cart");
        shoppingCartService.deleteCart(request.order_id());
        return ResponseEntity.ok().body("niuzie gege");
    }

}
