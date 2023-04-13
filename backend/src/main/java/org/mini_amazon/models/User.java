package org.mini_amazon.models;

import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid")
  private String id;
  private String username;
  private String password;
  private String email;

  @OneToMany(mappedBy = "owner")
  private Set<Order> orders;
}
