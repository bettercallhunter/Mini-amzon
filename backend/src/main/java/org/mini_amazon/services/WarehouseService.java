package org.mini_amazon.services;

import org.mini_amazon.models.Warehouse;
import org.mini_amazon.repositories.WarehouseRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

import jakarta.annotation.Resource;
@Service
public class WarehouseService {
  @Resource
  private WarehouseRepository warehouseRepository;

  public List<Warehouse> getAllWarehouses() {
    return warehouseRepository.findAll();
  }

  public Warehouse getWarehouseByDestination(int x, int y) {
    Optional<Warehouse> warehouse = warehouseRepository.findById(1);

    if (warehouse.isPresent()) {
      return warehouse.get();
    } else {
      System.out.println("not found");
      throw new RuntimeException("no warehouse found");
    }
  }
}
