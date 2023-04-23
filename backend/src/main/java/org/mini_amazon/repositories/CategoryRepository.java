package org.mini_amazon.repositories;

import org.mini_amazon.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
//  Category findByName(String name);
}
