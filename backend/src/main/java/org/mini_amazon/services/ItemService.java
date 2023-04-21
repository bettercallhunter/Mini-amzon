package org.mini_amazon.services;

import java.util.List;

import org.mini_amazon.models.Item;
import org.mini_amazon.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  @Autowired
  private ItemRepository itemRepository;

  public List<Item> getItems() {
    List<Item> items = itemRepository.findAll();
    return items;
  }
}
