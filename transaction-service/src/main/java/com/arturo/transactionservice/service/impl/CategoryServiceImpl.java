package com.arturo.transactionservice.service.impl;

import com.arturo.transactionservice.dto.response.CategoryDTO;
import com.arturo.transactionservice.entity.Category;
import com.arturo.transactionservice.enums.TransactionType;
import com.arturo.transactionservice.exception.ResourceNotFoundException;
import com.arturo.transactionservice.repository.CategoryRepository;
import com.arturo.transactionservice.service.CategoryService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    //Obtiene todas las categorías de la base de datos
    //Usa un stream para convertir cada Category en un CategoryDTO
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    //Filtra categorías según su tipo
    //TransactionType.INGRESO → categorías de ingreso
    //TransactionType.GASTO → categorías de gasto
    //Ideal para mostrar al usuario solo las categorías que aplican a su tipo de transacción
    @Override
    public List<CategoryDTO> getCategoriesByType(TransactionType type) {
        return categoryRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    //Busca una categoría por su ID
    //Si no existe, lanza una Excepcion(el globalexception lo maneja)
    //Si existe, la convierto en CategoryDTO y la devuelve
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con el ID: " + id));
        return mapToDTO(category);
    }

    @PostConstruct
    //Hace que el método se ejecute automaticamente al iniciar la aplicacion, justo despues de crear el bean
    @Transactional
    //Asegura que todas las operaciones dentro del método se ejecuten en una transacción atómica(si algo falla, se revierte todo
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Inicializando categorías predeterminadas...");

            List<Category> defaultCategories = new ArrayList<>();

            // Categorías de INGRESO
            defaultCategories.add(createCategory("Salario", "Ingresos por trabajo",
                    TransactionType.INGRESO, "attach_money", "#4CAF50", true));
            defaultCategories.add(createCategory("Freelance", "Trabajos independientes",
                    TransactionType.INGRESO, "work", "#8BC34A", true));
            defaultCategories.add(createCategory("Inversiones", "Retornos de inversión",
                    TransactionType.INGRESO, "trending_up", "#CDDC39", true));
            defaultCategories.add(createCategory("Ventas", "Venta de artículos",
                    TransactionType.INGRESO, "shopping_cart", "#9CCC65", true));
            defaultCategories.add(createCategory("Otros Ingresos", "Ingresos varios",
                    TransactionType.INGRESO, "more_horiz", "#C5E1A5", true));

            // Categorías de GASTO
            defaultCategories.add(createCategory("Alimentación", "Comidas y mercado",
                    TransactionType.GASTO, "restaurant", "#FF5722", true));
            defaultCategories.add(createCategory("Transporte", "Movilidad y combustible",
                    TransactionType.GASTO, "directions_car", "#FF9800", true));
            defaultCategories.add(createCategory("Vivienda", "Alquiler y servicios",
                    TransactionType.GASTO, "home", "#F44336", true));
            defaultCategories.add(createCategory("Entretenimiento", "Ocio y recreación",
                    TransactionType.GASTO, "movie", "#9C27B0", true));
            defaultCategories.add(createCategory("Salud", "Médico y medicinas",
                    TransactionType.GASTO, "local_hospital", "#E91E63", true));
            defaultCategories.add(createCategory("Educación", "Cursos y libros",
                    TransactionType.GASTO, "school", "#3F51B5", true));
            defaultCategories.add(createCategory("Servicios", "Luz, agua, internet",
                    TransactionType.GASTO, "build", "#607D8B", true));
            defaultCategories.add(createCategory("Ropa", "Vestuario y accesorios",
                    TransactionType.GASTO, "checkroom", "#795548", true));
            defaultCategories.add(createCategory("Tecnología", "Dispositivos y software",
                    TransactionType.GASTO, "computer", "#00BCD4", true));
            defaultCategories.add(createCategory("Otros Gastos", "Gastos varios",
                    TransactionType.GASTO, "more_horiz", "#9E9E9E", true));

            categoryRepository.saveAll(defaultCategories);
            log.info("Default categories initialized successfully!");
        }
    }

    //Crea un objeto Category con los datos recibidos
    //Se usa para construir fácilmente cada categoría predeterminada
    //Evita repetir código al crear muchas categorías con distintos valores
    private Category createCategory(String name, String description, TransactionType type,
                                    String iconName, String colorHex, boolean isDefault) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setType(type);
        category.setIconName(iconName);
        category.setColorHex(colorHex);
        category.setIsDefault(isDefault);
        return category;
    }

    //Convierte una entidad Category en su versión DTO
    //Separa la capa de datos(entidad) de la capa presentación(lo que se envía como JSON)
    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getType(),
                category.getIconName(),
                category.getColorHex(),
                category.getIsDefault()
        );
    }
}
