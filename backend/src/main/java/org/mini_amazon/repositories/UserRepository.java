package org.mini_amazon.repositories;

import org.mini_amazon.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
//  User findByUsername(String username);
}
