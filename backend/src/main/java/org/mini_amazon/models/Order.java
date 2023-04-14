package org.mini_amazon.models;

import org.mini_amazon.enums.OrderStatus;

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
@Table(name = "orders")
public class Order {
  @Id
  private long id;
  @ManyToOne
  private Item item;
  private double quantity;
  private double unitPrice;
  private double totalPrice;
//  @Enumerated(EnumType.STRING)
//  private OrderStatus status;
  @ManyToOne(cascade = {CascadeType.ALL})
  private Shipment shipment;
  @ManyToOne(cascade = {CascadeType.ALL})
  private User owner;
}
