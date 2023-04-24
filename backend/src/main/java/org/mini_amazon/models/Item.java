package org.mini_amazon.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;
  private String description;
  @Column(nullable = false)
  private double unitPrice;
  @ManyToMany(cascade = {
          CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  private Set<Category> categories;
  private String imgPath;

  private boolean onSale;

  //  @ManyToOne
//  @JsonIgnore
//  private User seller;
  // TODO: change to user
//  private String seller;

  public Item() {
    this.categories = new HashSet<>();
  }

  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = Objects.requireNonNullElse(description, "");
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public void setCategories(Set<Category> categories) {
    this.categories = Objects.requireNonNullElse(categories, new HashSet<>());
  }


  public String getImgPath() {
    return imgPath;
  }

  public void setImgPath(String imgPath) {
    this.imgPath = Objects.requireNonNullElse(imgPath, "");
  }

  public boolean isOnSale() {
    return onSale;
  }

  public void setOnSale(boolean onSale) {
    this.onSale = onSale;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Item item = (Item) o;
    return Double.compare(item.unitPrice, unitPrice) == 0 && onSale == item.onSale
           && Objects.equals(id, item.id) && Objects.equals(name, item.name)
           && Objects.equals(description, item.description)
           && Objects.equals(categories, item.categories)
           && Objects.equals(imgPath, item.imgPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, unitPrice, categories, imgPath, onSale);
  }

  @Override
  public String toString() {
    return "Item{" +
           "id=" + id +
           ", name='" + name + '\'' +
           ", description='" + description + '\'' +
           ", unitPrice=" + unitPrice +
           ", categories=" + categories +
           ", imgPath='" + imgPath + '\'' +
           ", onSale=" + onSale +
           '}';
  }
}
