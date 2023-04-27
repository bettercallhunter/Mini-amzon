package org.mini_amazon.repositories;

import org.mini_amazon.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
//  Category findByName(String name);
}
