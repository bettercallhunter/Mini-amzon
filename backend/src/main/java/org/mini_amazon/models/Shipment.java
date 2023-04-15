package org.mini_amazon.models;


import org.mini_amazon.enums.ShipmentStatus;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipments")
public class Shipment {
  @Id
  private String id;
//  @OneToMany(mappedBy = "shipment", cascade = {CascadeType.ALL})
//  private Set<Order> orders;
  @Enumerated(EnumType.STRING)
  private ShipmentStatus status;

}
