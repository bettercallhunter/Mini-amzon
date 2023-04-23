package org.mini_amazon.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "categories")
public class Category {

  @Id
  private String name;
  private String description; // optional
  @ManyToMany(mappedBy = "categories")
  @JsonIgnore
  private Set<Item> itemSet;

  public Category() {
    this.itemSet = new HashSet<>();
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

  public Set<Item> getItemSet() {
    return itemSet;
  }

  public void setItemSet(Set<Item> itemSet) {
    this.itemSet = itemSet;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Category category = (Category) o;
    return Objects.equals(name, category.name)
           && Objects.equals(description, category.description)
           && Objects.equals(itemSet, category.itemSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, itemSet);
  }

  @Override
  public String toString() {
    return "Category{" +
           "name='" + name + '\'' +
           ", description='" + description + '\'' +
           ", itemSet=" + itemSet +
           '}';
  }
}
