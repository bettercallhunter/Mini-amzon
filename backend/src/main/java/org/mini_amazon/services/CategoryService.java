package org.mini_amazon.services;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Category;
import org.mini_amazon.repositories.CategoryRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import jakarta.annotation.Resource;

@Service
public class CategoryService {
  @Resource
  private CategoryRepository categoryRepository;

  // if it has already existed, return it; otherwise, create a new category
  @Transactional
  public Category createCategory( String name,  String description) throws ServiceError {
    this.validate(name);
    Optional<Category> category = categoryRepository.findById(name);
    if (category.isEmpty()) {
      Category newCategory = new Category();
      newCategory.setName(name);
      newCategory.setDescription(description);
      return categoryRepository.save(newCategory);
    } else {
      return category.get();
    }
  }

  @Transactional(readOnly = true)
  public Category getCategoryById(String id) throws ServiceError {
    Optional<Category> category = categoryRepository.findById(id);
    if (category.isEmpty()) {
      throw new ServiceError("Category does not exist.");
    } else {
      return category.get();
    }
  }

  private void validate(String name) throws ServiceError {
    if (name == null || name.isEmpty()) {
      throw new ServiceError("Category name cannot be empty or null. ");
    }
  }
}
