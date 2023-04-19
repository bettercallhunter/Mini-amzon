package org.mini_amazon.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.mini_amazon.enums.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  private long id;

  @ManyToOne
  private Item item;

  private double quantity;

  @Deprecated
  private double unitPrice;

  @Deprecated
  private double totalPrice;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  //temp remove these for testing
  @ManyToOne(cascade = { CascadeType.ALL })
  private Shipment shipment;

  @ManyToOne(cascade = { CascadeType.ALL })
  private User owner;

  private String address;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }



  //
  //  public User getOwner() {
  //    return owner;
  //  }
  //
  //  public void setOwner(User owner) {
  //    this.owner = owner;
  //  }
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Shipment getShipment() {
    return shipment;
  }

  public void setShipment(Shipment shipment) {
    this.shipment = shipment;
  }
}
