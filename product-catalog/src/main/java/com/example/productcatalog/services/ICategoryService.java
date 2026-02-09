package com.example.productcatalog.services;

import com.example.productcatalog.models.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    Category deleteCategory(Long id);
    List<Category> getAllCategories();
}
