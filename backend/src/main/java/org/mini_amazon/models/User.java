package org.mini_amazon.models;

import com.google.common.hash.Hashing;
import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "users")
public class User {

  @Id
  private String email;

  private String username;
  private String password;
  private String salt;

  public User(String email, String username, String password) {
    this.email = email;
    this.username = username;
    this.password = password;
    Random r = new SecureRandom();
    byte[] Salt = new byte[20];
    r.nextBytes(Salt);
    this.salt = Base64.getEncoder().encodeToString(Salt);
  }

  public User() {
    Random r = new SecureRandom();
    byte[] Salt = new byte[20];
    r.nextBytes(Salt);
    this.salt = Base64.getEncoder().encodeToString(Salt);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean verifyPassword(String password) {
    final String hashed = Hashing
      .sha256()
      .hashString(password + salt, StandardCharsets.UTF_8)
      .toString();
    return hashed.equals(this.password);
  }

  public void setPassword(String password) {
    final String hashed = Hashing
      .sha256()
      .hashString(password + salt, StandardCharsets.UTF_8)
      .toString();
    this.password = hashed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return (
      Objects.equals(email, user.email) &&
      Objects.equals(username, user.username) &&
      Objects.equals(password, user.password) &&
      Objects.equals(salt, user.salt)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, username, password);
  }

  @Override
  public String toString() {
    return (
      "User{" +
      "email='" +
      email +
      '\'' +
      ", username='" +
      username +
      '\'' +
      ", password='" +
      password +
      '\'' +
      '}'
    );
  }
}
