package org.mini_amazon.repositories;

import java.util.List;
import org.mini_amazon.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Order findById(Long id);
    // Order findFirstByOrderByIdDesc();
    // List<Order> findAll();

  Page<Order> findAllByOwnerUsername( Pageable pageable,String username);
}
