package org.mini_amazon.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import org.mini_amazon.enums.ShipmentStatus;

@Entity
@Table(name = "shipments")
public class Shipment {

  @Id
  // @Column(name = "id", columnDefinition = "BIGINT")
  private long id;

  private String address;

  @OneToMany(mappedBy = "shipment", cascade = { CascadeType.ALL })
  @JsonIgnore
  private Set<Order> orders;

  @Enumerated(EnumType.STRING)
  private ShipmentStatus status;

  public long getId() {
    return id;
  }

  public Set<Order> getOrders() {
    return orders;
  }

  public ShipmentStatus getStatus() {
    return status;
  }

  public String getAddress() {
    return address;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setOrders(Set<Order> orders) {
    this.orders = orders;
  }

  public void setStatus(ShipmentStatus status) {
    this.status = status;
  }

  public Shipment(
    long id,
    String address,
    Set<Order> orders,
    ShipmentStatus status
  ) {
    this.id = id;
    this.address = address;
    this.orders = orders;
    this.status = status;
  }

  public void addOrder(Order order) {
    order.setShipment(this);
    this.orders.add(order);
  }

  public Shipment() {
    Set<Order> orders = new HashSet<>();
    this.orders = orders;
    this.status = ShipmentStatus.PENDING;
  }
}
