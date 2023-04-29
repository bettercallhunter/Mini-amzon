package org.mini_amazon.runner;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Warehouse;
import org.mini_amazon.repositories.WarehouseRepository;
import org.mini_amazon.services.CategoryService;
import org.mini_amazon.services.ItemService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSetUp implements CommandLineRunner {
  @Resource
  private CategoryService categoryService;
  @Resource
  private ItemService itemService;
  @Resource
  private WarehouseRepository warehouseRepository;

  @Override
  public void run(String... args) throws Exception {
    this.sendDatabase();
    System.out.println("Data set up finished. ");
  }

  private void sendDatabase() throws ServiceError {
    List<Pair<Integer, Integer>> positions = List.of(Pair.of(0, 0), Pair.of(1, 1),Pair.of(200,200));
    List<String> categoryList = List.of("food", "electronics");
    List<String> foodList = List.of("apple", "banana", "orange");
    List<String> electronicsList = List.of("computer", "phone", "tv");
    List<String> test = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      test.add("test" + i);
    }
    warehouseRepository.saveAll(positions.stream().map(p -> {
      Warehouse w = new Warehouse();
      w.setX(p.getFirst());
      w.setY(p.getSecond());
      return w;
    }).toList());

    for (String c : categoryList) {
      categoryService.createCategory(c, "this is a " + c + " category");
    }
    for (String i : foodList) {
      itemService.createItem(i, "this is a " + i, Math.random() * 100, Set.of("food"), "", true);
    }
    for (String i : electronicsList) {
      itemService.createItem(i,
              "this is a " + i, Math.random() * 100, Set.of("electronics"), "", true);
    }
    for (String i : test) {
      itemService.createItem(i,
              "this is a " + i, Math.random() * 100, Set.of("electronics"), "", true);
    }
  }

}
