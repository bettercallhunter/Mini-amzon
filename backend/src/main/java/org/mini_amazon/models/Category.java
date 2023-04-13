package org.mini_amazon.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouses")
public class Category {
  @Id
  private int id;
  private String name;
  private String description; // optional
  @ManyToMany(mappedBy = "items", cascade = {CascadeType.ALL})
  private Set<Item> itemSet;
}
