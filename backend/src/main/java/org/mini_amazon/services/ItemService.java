package org.mini_amazon.services;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Category;
import org.mini_amazon.models.Item;
import org.mini_amazon.repositories.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Resource;

@Service

public class ItemService {
  @Resource
  private ItemRepository itemRepository;
  @Resource
  private CategoryService categoryService;

  // add category to item
  @Transactional(readOnly = true)
  public Item getItemById(Long itemId) throws ServiceError {

    Optional<Item> item = itemRepository.findById(itemId);
    if (item.isPresent()) {
      return item.get();
    } else {
      throw new ServiceError("No item found. Please check the item id: " + itemId);
    }
  }

  @Transactional(readOnly = true)
  public Page<Item> listItems(Integer pageNo, Integer pageSize, String... sortBy) {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
    Page<Item> pagedResult = itemRepository.findAll(paging);
//    System.out.println(pagedResult);
    return pagedResult;
  }

  @Transactional(readOnly = true)
  public Page<Item> listOnSaleItems(Integer pageNo, Integer pageSize, String searchName, String... sortBy) {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
    Page<Item> pagedResult = itemRepository.findItemsByOnSaleAndNameContainingIgnoreCase(true, searchName, paging);
//    System.out.println(searchName);
    return pagedResult;
  }

  @Transactional
  public Item createItem(String name, String description, double unitPrice, Set<String> categoryIds, String imgPath, boolean onSale) throws ServiceError {
    this.validate(name, unitPrice);
    Item newItem = new Item();
    newItem.setName(name);
    newItem.setDescription(description);
    newItem.setUnitPrice(unitPrice);
    Set<Category> categories = new HashSet<>();
    for (String categoryId : categoryIds) {
      Category category = categoryService.getCategoryById(categoryId);
      categories.add(category);
    }
//    System.out.println(categories);
    newItem.setCategories(categories);
    newItem.setImgPath(imgPath);
    newItem.setOnSale(onSale);
    return itemRepository.save(newItem);
  }

  @Transactional
  public Item updateItem(long itemId, String userEmail, String name, String description, double unitPrice, Set<String> categoryIds, String imgPath, boolean onSale) throws ServiceError {

    Item item = this.getItemById(itemId);
    // TODO: check if item owner is the same as the user

    return this.createItem(name, description, unitPrice, categoryIds, imgPath, onSale);
  }

  @Transactional
  public void deleteItem(long itemId, String userEmail) throws ServiceError {
    Item item = this.getItemById(itemId);
    // TODO: check if item owner is the same as the user
    itemRepository.delete(item);
  }

  private void validate(String name, double unitPrice) throws ServiceError {
    if (name == null || name.isEmpty()) {
      throw new ServiceError("Name cannot be empty or null.");
    }
    if (unitPrice <= 0) {
      throw new ServiceError("Unit price cannot be less than or equal to 0.");
    }
  }
}
