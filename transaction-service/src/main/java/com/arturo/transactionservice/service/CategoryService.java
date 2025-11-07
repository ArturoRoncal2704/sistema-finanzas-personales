package com.arturo.transactionservice.service;

import com.arturo.transactionservice.dto.response.CategoryDTO;
import com.arturo.transactionservice.enums.TransactionType;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getCategoriesByType(TransactionType type);

    CategoryDTO getCategoryById(Long id);

    void initializeDefaultCategories();
}
