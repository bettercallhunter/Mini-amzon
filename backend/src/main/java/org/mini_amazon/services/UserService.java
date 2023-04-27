package org.mini_amazon.services;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.User;
import org.mini_amazon.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import jakarta.annotation.Resource;

@Service
public class UserService {

  @Resource
  private UserRepository userRepository;

  public User getUserByUsername(String username) throws ServiceError{
    Optional<User> optional = userRepository.findById(username);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new ServiceError("No user found. Please check the username: " + username);
    }
  }

  public User getCurrentUser() throws ServiceError {
    User parsedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = parsedUser.getUsername();
    return getUserByUsername(username);
  }
}
