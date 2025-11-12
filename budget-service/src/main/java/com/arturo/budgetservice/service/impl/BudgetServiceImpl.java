package com.arturo.budgetservice.service.impl;

import com.arturo.budgetservice.client.TransactionClient;
import com.arturo.budgetservice.dto.request.BudgetRequest;
import com.arturo.budgetservice.dto.response.BudgetResponse;
import com.arturo.budgetservice.dto.response.BudgetSummary;
import com.arturo.budgetservice.entity.Budget;
import com.arturo.budgetservice.entity.BudgetAlert;
import com.arturo.budgetservice.enums.AlertType;
import com.arturo.budgetservice.enums.BudgetPeriod;
import com.arturo.budgetservice.enums.BudgetStatus;
import com.arturo.budgetservice.exception.BadRequestException;
import com.arturo.budgetservice.exception.ResourceNotFoundException;
import com.arturo.budgetservice.repository.BudgetAlertRepository;
import com.arturo.budgetservice.repository.BudgetRepository;
import com.arturo.budgetservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    // Inyección de dependencias
    private final BudgetRepository budgetRepository;          // Repositorio para acceder a la tabla de presupuestos
    private final BudgetAlertRepository budgetAlertRepository; // Repositorio para manejar alertas de presupuesto
    private final TransactionClient transactionClient;         // Cliente Feign para comunicarse con el microservicio de transacciones

    // Crear un nuevo presupuesto
    @Override
    @Transactional // Garantiza que la operación se ejecute dentro de una transacción
    public BudgetResponse createBudget(BudgetRequest request, Long userId) {
        log.info("Creando un nuevo presupuesto para el usuario: {}", userId);

        // Validar que la fecha de fin sea posterior a la de inicio
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        // Crear entidad Budget desde el DTO
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setName(request.getName());
        budget.setCategoryId(request.getCategoryId());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setAlertThreshold(request.getAlertThreshold() != null ?
                request.getAlertThreshold() : new BigDecimal("80.00")); // Umbral por defecto: 80%
        budget.setIsActive(true);

        // Guardar en la base de datos
        Budget savedBudget = budgetRepository.save(budget);
        log.info("Presupuesto creado correctamente con ID: {}", savedBudget.getId());

        // Retornar el presupuesto con progreso calculado
        return getBudgetWithProgress(savedBudget.getId(), userId);
    }

    // Actualizar presupuesto existente
    @Override
    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request, Long userId) {
        log.info("Actualizando presupuesto {} para el usuario: {}", id, userId);

        // Buscar el presupuesto
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado."));

        // Validar fechas
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        // Actualizar datos
        budget.setName(request.getName());
        budget.setCategoryId(request.getCategoryId());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setAlertThreshold(request.getAlertThreshold() != null ?
                request.getAlertThreshold() : budget.getAlertThreshold());

        // Guardar cambios
        Budget updatedBudget = budgetRepository.save(budget);
        log.info("Presupuesto actualizado correctamente.");

        return getBudgetWithProgress(updatedBudget.getId(), userId);
    }

    // Eliminar un presupuesto
    @Override
    @Transactional
    public void deleteBudget(Long id, Long userId) {
        log.info("Eliminando presupuesto {} del usuario: {}", id, userId);

        // Verificar que exista
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado."));

        budgetRepository.delete(budget);
        log.info("Presupuesto eliminado correctamente.");
    }

    // Obtener un presupuesto por ID (con progreso calculado)
    @Override
    public BudgetResponse getBudgetById(Long id, Long userId) {
        return getBudgetWithProgress(id, userId);
    }

    //  Obtener todos los presupuestos del usuario
    @Override
    public List<BudgetResponse> getAllBudgets(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(budget -> getBudgetWithProgress(budget.getId(), userId)) // Calcula progreso para cada uno
                .collect(Collectors.toList());
    }

    //  Obtener solo los presupuestos activos
    @Override
    public List<BudgetResponse> getActiveBudgets(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActive(userId, true);
        return budgets.stream()
                .map(budget -> getBudgetWithProgress(budget.getId(), userId))
                .collect(Collectors.toList());
    }

    //  Obtener presupuestos filtrados por período (MENSUAL, ANUAL, etc.)
    @Override
    public List<BudgetResponse> getBudgetsByPeriod(Long userId, BudgetPeriod period) {
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(userId, period);
        return budgets.stream()
                .map(budget -> getBudgetWithProgress(budget.getId(), userId))
                .collect(Collectors.toList());
    }

    //  Obtener un resumen general de los presupuestos del usuario
    @Override
    public BudgetSummary getBudgetSummary(Long userId) {
        log.info("Obteniendo resumen de presupuestos para el usuario: {}", userId);

        // Buscar todos los presupuestos y los activos
        List<Budget> allBudgets = budgetRepository.findByUserId(userId);
        List<Budget> activeBudgets = budgetRepository.findByUserIdAndIsActive(userId, true);

        // Variables de conteo y acumulación
        int budgetsOnTrack = 0;     // Presupuestos dentro del límite
        int budgetsWithWarning = 0; // Presupuestos cerca del límite
        int budgetsExceeded = 0;    // Presupuestos excedidos
        BigDecimal totalBudgeted = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;

        // Calcular progreso para cada presupuesto activo
        for (Budget budget : activeBudgets) {
            totalBudgeted = totalBudgeted.add(budget.getAmount());

            // Consultar gasto real al microservicio de transacciones
            BigDecimal spent = transactionClient.calculateSpentAmount(
                    userId,
                    budget.getCategoryId(),
                    budget.getStartDate(),
                    budget.getEndDate()
            );

            totalSpent = totalSpent.add(spent);

            // Calcular porcentaje utilizado
            BigDecimal percentageUsed = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            // Clasificar estado del presupuesto
            if (percentageUsed.compareTo(BigDecimal.valueOf(100)) >= 0) {
                budgetsExceeded++;
            } else if (percentageUsed.compareTo(budget.getAlertThreshold()) >= 0) {
                budgetsWithWarning++;
            } else {
                budgetsOnTrack++;
            }
        }

        // Calcular total restante
        BigDecimal totalRemaining = totalBudgeted.subtract(totalSpent);

        // Crear objeto resumen
        return new BudgetSummary(
                allBudgets.size(),    // total de presupuestos
                activeBudgets.size(), // total activos
                budgetsOnTrack,
                budgetsWithWarning,
                budgetsExceeded,
                totalBudgeted,
                totalSpent,
                totalRemaining
        );
    }

    //  Obtener un presupuesto con su progreso actual (porcentaje, gasto, alertas, etc.)
    @Override
    @Transactional
    public BudgetResponse getBudgetWithProgress(Long id, Long userId) {
        // Buscar presupuesto
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado."));

        // Obtener el gasto total del microservicio de transacciones
        BigDecimal spent = transactionClient.calculateSpentAmount(
                userId,
                budget.getCategoryId(),
                budget.getStartDate(),
                budget.getEndDate()
        );

        // Calcular saldo restante y porcentaje de uso
        BigDecimal remaining = budget.getAmount().subtract(spent);
        BigDecimal percentageUsed = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // Determinar el estado del presupuesto
        BudgetStatus status;
        if (percentageUsed.compareTo(BigDecimal.valueOf(100)) >= 0) {
            status = BudgetStatus.EXCEEDED;
            createAlertIfNeeded(budget, percentageUsed, AlertType.EXCEEDED);
        } else if (percentageUsed.compareTo(budget.getAlertThreshold()) >= 0) {
            status = BudgetStatus.WARNING;
            createAlertIfNeeded(budget, percentageUsed, AlertType.WARNING);
        } else {
            status = BudgetStatus.ON_TRACK;
        }

        // Construir respuesta DTO
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setUserId(budget.getUserId());
        response.setName(budget.getName());
        response.setCategory(null); // (Puede completarse con datos del microservicio de categorías)
        response.setAmount(budget.getAmount());
        response.setSpent(spent);
        response.setRemaining(remaining);
        response.setPercentageUsed(percentageUsed);
        response.setStartDate(budget.getStartDate());
        response.setEndDate(budget.getEndDate());
        response.setPeriod(budget.getPeriod());
        response.setStatus(status);
        response.setAlertThreshold(budget.getAlertThreshold());
        response.setIsActive(budget.getIsActive());
        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());

        return response;
    }

    //  Crear alerta si el presupuesto se acerca o supera el límite
    private void createAlertIfNeeded(Budget budget, BigDecimal percentageUsed, AlertType alertType) {
        // Verificar si ya existe una alerta activa del mismo tipo
        List<BudgetAlert> existingAlerts = budgetAlertRepository.findByBudgetId(budget.getId());

        boolean alertExists = existingAlerts.stream()
                .anyMatch(alert -> alert.getType() == alertType && !alert.getIsRead());

        // Si no existe una alerta activa, crear una nueva
        if (!alertExists) {
            BudgetAlert alert = new BudgetAlert();
            alert.setBudgetId(budget.getId());
            alert.setUserId(budget.getUserId());
            alert.setType(alertType);
            alert.setPercentageUsed(percentageUsed);
            alert.setIsRead(false);

            // Mensaje de alerta según el tipo
            String message = alertType == AlertType.WARNING ?
                    String.format("El presupuesto '%s' ha alcanzado el %.2f%% de su límite asignado.",
                            budget.getName(), percentageUsed) :
                    String.format("El presupuesto '%s' ha sido excedido (%.2f%%).",
                            budget.getName(), percentageUsed);

            alert.setMessage(message);

            // Guardar alerta
            budgetAlertRepository.save(alert);
            log.info("Alerta de presupuesto creada para el presupuesto ID: {}", budget.getId());
        }
    }
}