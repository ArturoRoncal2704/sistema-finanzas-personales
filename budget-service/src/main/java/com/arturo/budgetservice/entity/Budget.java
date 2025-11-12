package com.arturo.budgetservice.entity;

import com.arturo.budgetservice.enums.BudgetPeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", indexes = {
    //Crea índices en columnas usadas frecuentemente en búsquedas, como user_id, start_date, end_date, o is_active.
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "category_id")
    private Long categoryId;

    //Permite valores con 2 decimales
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    //Se guarda por String no por digito
    @Column(nullable = false)
    private BudgetPeriod period = BudgetPeriod.MENSUAL; //valor por defecto mensual


    @Column(name = "alert_threshold", precision = 5, scale = 2)
    private BigDecimal alertThreshold = new BigDecimal("80.00"); //Porcentaje de gasto a partir del cual se activa una alerta
    //Valor por defecto 80%
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}