package org.mini_amazon.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouses")
public class Warehouse {
  @Id
  private int id;
  private int x;
  private int y;
}
