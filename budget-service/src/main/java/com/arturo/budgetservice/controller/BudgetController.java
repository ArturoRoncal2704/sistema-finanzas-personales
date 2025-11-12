package com.arturo.budgetservice.controller;

import com.arturo.budgetservice.dto.request.BudgetRequest;
import com.arturo.budgetservice.dto.response.BudgetResponse;
import com.arturo.budgetservice.dto.response.BudgetSummary;
import com.arturo.budgetservice.dto.response.MessageResponse;
import com.arturo.budgetservice.enums.BudgetPeriod;
import com.arturo.budgetservice.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

    // Inyección del servicio que contiene la lógica de negocio
    private final BudgetService budgetService;

    // Crear un nuevo presupuesto
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,      // El cuerpo de la solicitud (validado)
            @RequestHeader("X-User-Id") Long userId) {      // Se obtiene el ID del usuario desde el encabezado HTTP
        log.info("Solicitud de creación de presupuesto para el usuario: {}", userId);

        // Llama al servicio para crear el presupuesto
        BudgetResponse response = budgetService.createBudget(request, userId);

        // Devuelve el presupuesto creado con el código de estado 201 (CREATED)
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Actualizar un presupuesto existente
    @PutMapping("/{id}") // 📍 PUT /budgets/{id}
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,                         // ID del presupuesto a actualizar
            @Valid @RequestBody BudgetRequest request,     // Nuevos datos del presupuesto
            @RequestHeader("X-User-Id") Long userId) {     // Usuario autenticado
        log.info("Solicitud de actualización del presupuesto {} para el usuario: {}", id, userId);

        // Actualiza el presupuesto y devuelve la respuesta
        BudgetResponse response = budgetService.updateBudget(id, request, userId);
        return ResponseEntity.ok(response);
    }

    //  Eliminar un presupuesto
    @DeleteMapping("/{id}") // DELETE /budgets/{id}
    public ResponseEntity<MessageResponse> deleteBudget(
            @PathVariable Long id,                         // ID del presupuesto
            @RequestHeader("X-User-Id") Long userId) {     // Usuario autenticado
        log.info("Solicitud de eliminación del presupuesto {} para el usuario: {}", id, userId);

        // Llama al servicio para eliminar el presupuesto
        budgetService.deleteBudget(id, userId);

        // Devuelve un mensaje de éxito
        return ResponseEntity.ok(new MessageResponse("Presupuesto eliminado correctamente."));
    }

    // Obtener un presupuesto por ID
    @GetMapping("/{id}") // GET /budgets/{id}
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long id,                         // ID del presupuesto
            @RequestHeader("X-User-Id") Long userId) {     // Usuario autenticado
        log.info("Solicitud de obtención del presupuesto {} para el usuario: {}", id, userId);

        // Obtiene el presupuesto del servicio
        BudgetResponse response = budgetService.getBudgetById(id, userId);
        return ResponseEntity.ok(response);
    }

    //  Obtener todos los presupuestos (con filtros opcionales)
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @RequestHeader("X-User-Id") Long userId,           //  Usuario autenticado
            @RequestParam(required = false) Boolean active,    // Filtro opcional: solo activos
            @RequestParam(required = false) BudgetPeriod period) { //  Filtro opcional: por período (mensual, anual, etc.)
        log.info("Solicitud de listado de presupuestos para el usuario: {}", userId);

        List<BudgetResponse> budgets;

        // Si el parámetro "active" es true → devuelve solo los activos
        if (active != null && active) {
            budgets = budgetService.getActiveBudgets(userId);
        }
        // Si se especifica el período → filtra por tipo de período
        else if (period != null) {
            budgets = budgetService.getBudgetsByPeriod(userId, period);
        }
        // Si no hay filtros → devuelve todos
        else {
            budgets = budgetService.getAllBudgets(userId);
        }

        return ResponseEntity.ok(budgets);
    }

    //  Obtener un resumen general de los presupuestos del usuario
    @GetMapping("/summary") //  GET /budgets/summary
    public ResponseEntity<BudgetSummary> getBudgetSummary(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Solicitud de resumen de presupuestos para el usuario: {}", userId);

        // Llama al servicio para generar el resumen (totales, advertencias, etc.)
        BudgetSummary summary = budgetService.getBudgetSummary(userId);
        return ResponseEntity.ok(summary);
    }

    // Obtener el progreso actual de un presupuesto (porcentaje usado, restante, etc.)
    @GetMapping("/{id}/progress") //  GET /budgets/{id}/progress
    public ResponseEntity<BudgetResponse> getBudgetProgress(
            @PathVariable Long id,                         // ID del presupuesto
            @RequestHeader("X-User-Id") Long userId) {     // Usuario autenticado
        log.info("Solicitud de progreso del presupuesto {} para el usuario: {}", id, userId);

        // Llama al servicio para calcular el progreso
        BudgetResponse response = budgetService.getBudgetWithProgress(id, userId);
        return ResponseEntity.ok(response);
    }
}
