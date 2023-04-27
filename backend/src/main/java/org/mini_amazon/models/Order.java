package org.mini_amazon.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.mini_amazon.enums.OrderStatus;

import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Item item;

  private int quantity;

  // @Deprecated
  // private double unitPrice;
  //
  // @Deprecated
  // private double totalPrice;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  // @JsonIgnore
  @ManyToOne
  private Shipment shipment;

  @ManyToOne
  @JsonIgnore
  private User owner;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

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

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", item=" + item +
        ", quantity=" + quantity +
        ", status=" + status +
        ", shipment=" + shipment +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Order order = (Order) o;
    return quantity == order.quantity && Objects.equals(id, order.id)
        && Objects.equals(item, order.item) && status == order.status
        && Objects.equals(shipment, order.shipment)
        && Objects.equals(owner, order.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, item, quantity, status, shipment, owner);
  }
}
