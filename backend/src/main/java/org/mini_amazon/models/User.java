package org.mini_amazon.models;


import org.mini_amazon.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  private String username;
  @Column(nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  //  private String salt;
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @OneToMany(mappedBy = "owner",orphanRemoval = true)
  private List<Order> cart;

  public User(String email, String username, String password) {
    // this.email = email;
    // this.username = username;
    // this.password = password;
    // Random r = new SecureRandom();
    // byte[] Salt = new byte[20];
    // r.nextBytes(Salt);
    // this.salt = Base64.getEncoder().encodeToString(Salt);
    // this.roles = Set.of(Role.BUYER);
  }

  public User() {
//    Random r = new SecureRandom();
//    byte[] Salt = new byte[20];
//    r.nextBytes(Salt);
//    this.salt = Base64.getEncoder().encodeToString(Salt);
    this.roles = Set.of(Role.BUYER);
    this.cart = new ArrayList<>();
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  // public String getSalt() {
  // return salt;
  // }
  //
  // public void setSalt(String salt) {
  // this.salt = salt;
  // }

  // from user details

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (Role role : roles) {
      authorities.add(new SimpleGrantedAuthority(role.name()));
    }
    return authorities;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public List<Order> getCart() {
    return cart;
  }

  public void setCart(List<Order> cart) {
    this.cart = cart;
  }

  public void addCart(Order order) {
    this.cart.add(order);
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  @Override
  public String toString() {
    return "User{" +
           "username='" + username + '\'' +
           ", email='" + email + '\'' +
           ", password='" + password + '\'' +
           ", roles=" + roles +
           ", cart=" + cart +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return Objects.equals(username, user.username)
           && Objects.equals(email, user.email)
           && Objects.equals(password, user.password)
           && Objects.equals(roles, user.roles)
           && Objects.equals(cart, user.cart);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email, password, roles, cart);
  }
}
