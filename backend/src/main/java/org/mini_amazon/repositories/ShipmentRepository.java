package org.mini_amazon.repositories;

import org.mini_amazon.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
  Shipment findByAddress(String address);
  Shipment findFirstByOrderByIdDesc();
}
