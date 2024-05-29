package com.vhais.blog.service;

import com.vhais.blog.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category saveCategory(Category category);
    Optional<Category> getCategoryByName(String name);
    List<Category> getAllCategories();
}
