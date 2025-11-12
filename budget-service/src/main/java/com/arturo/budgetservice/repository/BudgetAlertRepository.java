package com.arturo.budgetservice.repository;

import com.arturo.budgetservice.entity.BudgetAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, Long> {
    
    List<BudgetAlert> findByUserId(Long userId);
    //Busca alertas por usuario

    List<BudgetAlert> findByUserIdAndIsRead(Long userId, Boolean isRead);
    //Buscar alertas por usuario y si están leidas o no

    List<BudgetAlert> findByBudgetId(Long budgetId);
    //Buscar alertas de un presupuesto específico

    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    //Contar alertas no leídas de un usuario
}