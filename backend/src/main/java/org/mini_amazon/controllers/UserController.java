package org.mini_amazon.controllers;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.mini_amazon.services.OrderService;
import org.mini_amazon.services.UserService;
import org.mini_amazon.utils.JwtTokenUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class UserController {

  @Resource
  private UserRepository userRepository;

  @Resource
  private UserService userService;

  record registerRequest(String username, String email, String password) {}

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody registerRequest request) {
    userService.createUser(
      request.username(),
      request.email(),
      request.password()
    );
    return ResponseEntity.ok("niudeniude");
  }

  record loginRequest(String username, String password) {}

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody loginRequest request) {
    try {
      HttpHeaders headers = userService.getUser(
        request.username(),
        request.password()
      );
      return ResponseEntity.ok().headers(headers).body("ojbk");
    } catch (Exception e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
}
