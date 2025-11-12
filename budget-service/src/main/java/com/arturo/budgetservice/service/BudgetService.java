package com.arturo.budgetservice.service;

import com.arturo.budgetservice.dto.request.BudgetRequest;
import com.arturo.budgetservice.dto.response.BudgetResponse;
import com.arturo.budgetservice.dto.response.BudgetSummary;
import com.arturo.budgetservice.enums.BudgetPeriod;

import java.util.List;

public interface BudgetService {

    //Crear presupuesto
    BudgetResponse createBudget(BudgetRequest request, Long userId);

    //Actualizar presupuesto
    BudgetResponse updateBudget(Long id, BudgetRequest request, Long userId);

    //Eliminar presupuesto
    void deleteBudget(Long id, Long userId);

    //Obtener presupuesto por ID
    BudgetResponse getBudgetById(Long id, Long userId);

    //Listar todos los presupuestos
    List<BudgetResponse> getAllBudgets(Long userId);

    //Listar solo los activos
    List<BudgetResponse> getActiveBudgets(Long userId);

    //Filtrar por período
    List<BudgetResponse> getBudgetsByPeriod(Long userId, BudgetPeriod period);

    //Resumen general de presupuestos
    BudgetSummary getBudgetSummary(Long userId);

    //Obtener un presupuesto por progreso actual
    BudgetResponse getBudgetWithProgress(Long id, Long userId);
}