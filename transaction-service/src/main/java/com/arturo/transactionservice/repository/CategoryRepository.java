package com.arturo.transactionservice.repository;

import com.arturo.transactionservice.entity.Category;
import com.arturo.transactionservice.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByType(TransactionType type);

    List<Category> findByIsDefaultTrue();

    boolean existsByName (String name);
}
