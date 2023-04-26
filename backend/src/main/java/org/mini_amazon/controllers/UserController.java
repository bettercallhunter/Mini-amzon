package org.mini_amazon.controllers;

import jakarta.annotation.Resource;

import org.mini_amazon.models.User;
import org.mini_amazon.services.AuthService;
//import org.mini_amazon.utils.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

  @Resource
  private AuthService authService;

  public record LoginRequest(String username, String password) {
  }

  public record RegisterRequest(String username, String email, String password, String password2) {
  }

  public record AuthenticationResponse(String token, String error) {
  }


  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    AuthenticationResponse response = authService.register(request);
//    System.out.println("register response: " + response.token() + " " + response.error());
    if (response.token() == null || response.error() != null) {
      return ResponseEntity.status(400).body(response);
    } else {
      return ResponseEntity.ok(response);
    }
  }


  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
    AuthenticationResponse response = authService.authenticate(request);
//    System.out.println("login response: " + response.token() + " " + response.error());
    String token = response.token();
    if (token == null || response.error() != null) {
      return ResponseEntity.status(400).body(response);
    } else {
//      System.out.println(ResponseEntity.ok(response));
//      ResponseCookie cookie = ResponseCookie
//              .from("token",  token)
////              .httpOnly(false)
//              .path("/")
//              .build();
//      HttpHeaders headers = new HttpHeaders();
//
//      headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
      return ResponseEntity.ok().body(response);
    }
//    try {
//      User account = userRepository.findById(request.username());
//      boolean authentication = account.verifyPassword(request.password());
//      if (!authentication) {
//        throw new Exception("Wrong password");
//      }
//
//      Map<String, Object> claims = new HashMap<>();
//      claims.put("user", account);
//      String token = JwtTokenUtil.generateToken(claims);
//      System.out.println("token is " + token);
//      // Create a cookie with the JWT token and add it to the response
//      ResponseCookie cookie = ResponseCookie
//              .from("jwt", token)
//              .httpOnly(true)
//              .path("/")
//              .build();
//      HttpHeaders headers = new HttpHeaders();
//
//      headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
//
//      User parsed_User = JwtTokenUtil.getClaimsFromToken(token);
//
//      System.out.println("parse username:" + parsed_User.getUsername());
//
//      return ResponseEntity.ok().headers(headers).body("ojbk");
//    } catch (Exception e) {
//      return ResponseEntity.status(404).body(e.getMessage());
//    }
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
//    System.out.println("token is " + token);
    System.out.println("reach health");
//    User parsed_User = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ResponseEntity.ok().body("hello");
  }
}
