package org.mini_amazon.repositories;

import org.mini_amazon.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findFirstByOrderByIdDesc();
}