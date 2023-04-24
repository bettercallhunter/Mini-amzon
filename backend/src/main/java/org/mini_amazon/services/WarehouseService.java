package org.mini_amazon.services;

import org.mini_amazon.errors.ServiceError;
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

  public Warehouse getWarehouseByDestination(int x, int y) throws ServiceError {
//    Optional<Warehouse> warehouse = warehouseRepository.findById(1);
    List<Warehouse> warehouses = warehouseRepository.findAll();
    if (warehouses.isEmpty()) {
      throw new ServiceError("no warehouse exists");
    } else {
      // find the closest warehouse based on x and y
      Warehouse closestWarehouse = warehouses.get(0);
      for (int i = 1; i < warehouses.size(); i++) {
        int distance = getDistance(x, y, closestWarehouse.getX(), closestWarehouse.getY());
        int newDistance = getDistance(x, y, warehouses.get(i).getX(), warehouses.get(i).getY());
        if (newDistance < distance) {
          closestWarehouse = warehouses.get(i);
        }
      }
      return closestWarehouse;
    }
  }

  private int getDistance(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }
}
