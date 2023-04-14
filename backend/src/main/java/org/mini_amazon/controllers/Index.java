package org.mini_amazon.controllers;

import java.util.List;

import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.mini_amazon.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class Index {

  @Resource
  private UserRepository userRepository;

  public static void main(String[] args) {
    SpringApplication.run(Index.class, args);
  }

  @GetMapping("/")
  public List<User> getAccounts() {
    return userRepository.findAll();
  }

  record registerRequest(String username, String email, String password) {
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody registerRequest request) {
//    Account newAccount = new Account();
//    newAccount.setEmail(request.email());
//    newAccount.setPassword(request.password());
//    newAccount.setUsername(request.username());
//    userRepository.save(newAccount);
    return ResponseEntity.ok("niudeniude");
  }

  record loginRequest(String username, String password) {
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody loginRequest request) {
    try {
      User account = userRepository.findByUsername(request.username());
      String token = JwtUtil.generateToken(account.getUsername());
      System.out.println(token);

      return ResponseEntity.ok("ojbk");
    } catch (Exception e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
  // class hreetResponse{
  // private final String msg;
  // hreetResponse(String b){
  // this.msg = b;
  // }
  // public String gethreet(){
  // return msg;
  // }
  // @Override
  // public String toString(){
  // return "Greet response{" + msg +"}";
  // }

}
