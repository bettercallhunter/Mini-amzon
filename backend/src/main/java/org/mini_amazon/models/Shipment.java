package org.mini_amazon.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.mini_amazon.enums.ShipmentStatus;

@Entity
@Table(name = "shipments")
public class Shipment {

  @Id
  @GeneratedValue
  private Long id;

  // destination
  // private String address;
  @Column(nullable = false)
  private double totalPrice;

  private Integer truckId;

  @Column(nullable = false)
  private int destinationX;
  @Column(nullable = false)
  private int destinationY;
  @ManyToOne
  // @JsonIgnore
  // @Column(nullable = false)
  private Warehouse warehouse;

  private String upsName;

  public String getUpsName() {
    return upsName;
  }

  public void setUpsName(String upsName) {
    this.upsName = upsName;
  }

  @OneToMany(mappedBy = "shipment", cascade = {CascadeType.ALL })
  @Column(nullable = false)
  @JsonIgnore
  private List<Order> orders;

  @ManyToOne(cascade = { CascadeType.ALL })
  @JsonIgnore
  // TODO
  private User owner;

  @Enumerated(EnumType.STRING)
  private ShipmentStatus status;

  public boolean canUpdatedTo(ShipmentStatus status) {
    return switch (status) {
      case PENDING -> true;
      case PACKED -> this.status == ShipmentStatus.PENDING;
      case LOADED -> this.status == ShipmentStatus.PACKED;
      case SHIPPING -> this.status == ShipmentStatus.LOADED;
      case DELIVERED -> this.status == ShipmentStatus.SHIPPING;
      case CANCELLED -> this.status != ShipmentStatus.DELIVERED;
    };
  }

  public Integer getTruckId() {
    return truckId;
  }

  public void setTruckId(Integer truckId) {
    this.truckId = truckId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public int getDestinationX() {
    return destinationX;
  }

  public void setDestinationX(int destinationX) {
    this.destinationX = destinationX;
  }

  public int getDestinationY() {
    return destinationY;
  }

  public void setDestinationY(int destinationY) {
    this.destinationY = destinationY;
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public void addOrder(Order order) {
    this.orders.add(order);
  }

  public ShipmentStatus getStatus() {
    return status;
  }

  public void setStatus(ShipmentStatus status) {
    this.status = status;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Shipment() {
    this.orders = new ArrayList<>();
    this.status = ShipmentStatus.PENDING;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Shipment shipment = (Shipment) o;
    return destinationX == shipment.destinationX && destinationY == shipment.destinationY
        && Objects.equals(id, shipment.id)
        && Objects.equals(orders, shipment.orders) && status == shipment.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, destinationX, destinationY, orders, status);
  }

  @Override
  public String toString() {
    return "Shipment{" +
        "id=" + id +
        ", destinationX=" + destinationX +
        ", destinationY=" + destinationY +
        ", truckId=" + truckId +
        // ", orders=" + Objects.requireNonNullElse(orders,"") +
        ", status=" + status +
        '}';
  }
}
