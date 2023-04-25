package org.mini_amazon.repositories;

import org.mini_amazon.models.Category;
import org.mini_amazon.models.Item;
import org.mini_amazon.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findAllByOnSale(boolean onSale, Pageable pageable);

  Page<Item> findItemsByOnSaleAndNameContainingIgnoreCase(boolean onSale, String name, Pageable pageable);

}
