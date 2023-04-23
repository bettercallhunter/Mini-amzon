package org.mini_amazon.models;


import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouses")
public class Warehouse {
  @Id
  @GeneratedValue
  private int id;
  private int x;
  private int y;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Warehouse warehouse = (Warehouse) o;
    return id == warehouse.id && x == warehouse.x && y == warehouse.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, x, y);
  }

  @Override
  public String toString() {
    return "Warehouse{" +
           "id=" + id +
           ", x=" + x +
           ", y=" + y +
           '}';
  }
}
