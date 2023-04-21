package org.mini_amazon.services;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.mini_amazon.utils.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Resource
  private UserRepository userRepository;

  public void createUser(String username, String email, String password) {
    User newAccount = new User();
    newAccount.setEmail(email);
    newAccount.setPassword(password);
    newAccount.setUsername(username);
    userRepository.save(newAccount);
  }

  public HttpHeaders getUser(String username, String password)
    throws Exception {
    User account = userRepository.findByUsername(username);
    boolean authentication = account.verifyPassword(password);
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
    return headers;
  }
}
