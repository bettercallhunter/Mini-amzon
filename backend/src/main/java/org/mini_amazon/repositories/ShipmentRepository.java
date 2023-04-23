package org.mini_amazon.repositories;

import org.mini_amazon.enums.ShipmentStatus;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

  Shipment findUnfinishedShipmentById(long id);

  List<Shipment> findShipmentsByStatusAndWarehouseId(ShipmentStatus status, int warehouseId);
}
