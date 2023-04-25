package org.mini_amazon.services;

import org.mini_amazon.controllers.UserController;
import org.mini_amazon.enums.Role;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import jakarta.annotation.Resource;

@Service
public class AuthService {

  @Resource
  private PasswordEncoder passwordEncoder;
  @Resource
  private UserRepository userRepository;
  @Resource
  private JwtService jwtService;
  @Resource
  private AuthenticationManager authenticationManager;

  public UserController.AuthenticationResponse authenticate(UserController.LoginRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    User user = userRepository.findById(request.username()).orElseThrow();
    String token = jwtService.generateToken(user);
    return new UserController.AuthenticationResponse(token, null);
  }

  public UserController.AuthenticationResponse register(UserController.RegisterRequest request) {
    if (!request.password().equals(request.password2())) {
      return new UserController.AuthenticationResponse(null,"Passwords do not match");
    } else {
      User user = new User();
      user.setUsername(request.username());
      user.setEmail(request.email());
      user.setPassword(passwordEncoder.encode(request.password()));
      user.setRoles(Set.of(Role.BUYER));
      userRepository.save(user);
      String token = jwtService.generateToken(user);
      return new UserController.AuthenticationResponse(token, null);
    }
  }
}
