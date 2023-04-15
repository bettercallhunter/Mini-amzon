package org.mini_amazon.repositories;

import org.mini_amazon.models.Item;
import org.mini_amazon.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
  Item findById(int id);
}
