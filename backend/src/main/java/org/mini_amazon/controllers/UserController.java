package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.List;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.mini_amazon.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class UserController {

  @Resource
  private UserRepository userRepository;

  record registerRequest(String username, String email, String password) {}

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody registerRequest request) {
    User newAccount = new User();
    newAccount.setEmail(request.email());
    newAccount.setPassword(request.password());
    newAccount.setUsername(request.username());
    userRepository.save(newAccount);
    return ResponseEntity.ok("niudeniude");
  }

  record loginRequest(String username, String password) {}

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody loginRequest request) {
    try {
      User account = userRepository.findByUsername(request.username());
      boolean authentication = account.verifyPassword(request.password());
      if (!authentication) {
        throw new Exception("Wrong password");
      }

      Map<String, Object> claims = new HashMap<>();
      claims.put("user", account);
      String token = JwtTokenUtil.generateToken(claims);
      System.out.println("token is " + token);
      // Create a cookie with the JWT token and add it to the response
      ResponseCookie cookie = ResponseCookie
        .from("jwt", token)
        .httpOnly(true)
        .path("/")
        .build();
      HttpHeaders headers = new HttpHeaders();

      headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

      User parsed_User = JwtTokenUtil.getClaimsFromToken(token);

      System.out.println("parse username:" + parsed_User.getUsername());

      return ResponseEntity.ok().headers(headers).body("ojbk");
    } catch (Exception e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
}
