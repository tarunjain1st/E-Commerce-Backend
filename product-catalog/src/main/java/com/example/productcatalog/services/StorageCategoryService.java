package com.example.productcatalog.services;

import com.example.productcatalog.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageCategoryService implements ICategoryService {
    @Override
    public Category getCategoryById(Long id) {
        return null;
    }

    @Override
    public Category getCategoryByName(String name) {
        return null;
    }

    @Override
    public Category createCategory(Category category) {
        return null;
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return null;
    }

    @Override
    public Category deleteCategory(Long id) {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }
}
