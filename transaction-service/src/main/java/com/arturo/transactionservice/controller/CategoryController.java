package com.arturo.transactionservice.controller;

import com.arturo.transactionservice.dto.response.CategoryDTO;
import com.arturo.transactionservice.enums.TransactionType;
import com.arturo.transactionservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    //Devuelve todas las categorías(ingresos + gastos)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("Obtener solicitud de todas las categorías");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        //Llama a categoryService.getAllCategories() → convierte entidades a DTOs → las devuelve en formato JSON.
        return ResponseEntity.ok(categories);
        //200 OK con un List<CategoryDTO>
    }
    
    @GetMapping("/type/{type}")
    //Devuelve solo las categorías del tipo solicitado.
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable TransactionType type) {
        log.info("Obtener categorías por tipo: {}", type);
        List<CategoryDTO> categories = categoryService.getCategoriesByType(type);
        //{type} → valor del enum TransactionType (ej. INGRESO o GASTO).
        return ResponseEntity.ok(categories);
        //Lista de categorías de ese tipo.
    }
    
    @GetMapping("/{id}")
    //Devuelve una categoría específica según su ID.
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("Obtener categoría por id: {}", id);
        //{id} → identificador de la categoría.
        CategoryDTO category = categoryService.getCategoryById(id);
        //200 OK con un objeto CategoryDTO.
        return ResponseEntity.ok(category);
    }
}