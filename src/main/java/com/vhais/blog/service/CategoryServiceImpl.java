package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public Category saveCategory(Category category) {
        return repository.save(category);
    }

    @Override
    public Optional<Category> getCategoryByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return repository.findAll();
    }
}
