package com.backend;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class Index {

  private final AccountRepository accountRepository;

  public Index(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public static void main(String[] args) {
    SpringApplication.run(Index.class, args);
  }

  @GetMapping("/")
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }

  record registerRequest(String username, String email, String password) {}

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody registerRequest request) {
    Account newAccount = new Account();
    newAccount.setEmail(request.email());
    newAccount.setPassword(request.password());
    newAccount.setUsername(request.username());
    accountRepository.save(newAccount);
    return ResponseEntity.ok("niudeniude");
  }

  record loginRequest(String username, String password) {}

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody loginRequest request) {
    try {
      Account account = accountRepository.findByUsername(request.username());
       String token = JwtUtil.generateToken(account.getUsername());
       System.out.println(token);

      return ResponseEntity.ok("ojbk");
    } catch (Exception e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
  //    class hreetResponse{
  //        private final String msg;
  //        hreetResponse(String b){
  //            this.msg = b;
  //        }
  //        public String gethreet(){
  //            return msg;
  //        }
  //        @Override
  //        public String toString(){
  //            return "Greet response{" + msg +"}";
  //        }

}
