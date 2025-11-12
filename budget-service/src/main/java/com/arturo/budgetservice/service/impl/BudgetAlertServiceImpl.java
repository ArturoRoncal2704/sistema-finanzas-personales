package com.arturo.budgetservice.service.impl;

import com.arturo.budgetservice.dto.response.BudgetAlertDTO;
import com.arturo.budgetservice.entity.Budget;
import com.arturo.budgetservice.entity.BudgetAlert;
import com.arturo.budgetservice.exception.BadRequestException;
import com.arturo.budgetservice.exception.ResourceNotFoundException;
import com.arturo.budgetservice.repository.BudgetAlertRepository;
import com.arturo.budgetservice.repository.BudgetRepository;
import com.arturo.budgetservice.service.BudgetAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertServiceImpl implements BudgetAlertService {

    // Dependencias inyectadas automáticamente por Spring
    private final BudgetAlertRepository budgetAlertRepository; // Acceso a la tabla de alertas
    private final BudgetRepository budgetRepository;           // Acceso a la tabla de presupuestos

    //Obtener todas las alertas de un usuario (leídas y no leídas)
    @Override
    public List<BudgetAlertDTO> getAllAlerts(Long userId) {
        // Busca todas las alertas del usuario en la base de datos
        List<BudgetAlert> alerts = budgetAlertRepository.findByUserId(userId);

        // Convierte cada entidad BudgetAlert a un DTO para devolver al cliente
        return alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtener solo las alertas no leídas de un usuario
    @Override
    public List<BudgetAlertDTO> getUnreadAlerts(Long userId) {
        // Busca todas las alertas con isRead = false
        List<BudgetAlert> alerts = budgetAlertRepository.findByUserIdAndIsRead(userId, false);

        // Las convierte en DTOs legibles
        return alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Marcar una alerta específica como leída
    @Override
    @Transactional // Se ejecuta dentro de una transacción de base de datos
    public void markAlertAsRead(Long alertId, Long userId) {
        // Buscar la alerta por ID
        BudgetAlert alert = budgetAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la alerta."));

        // Verificar que la alerta pertenezca al usuario
        if (!alert.getUserId().equals(userId)) {
            throw new BadRequestException("La alerta no pertenece al usuario.");
        }

        // Marcar como leída
        alert.setIsRead(true);

        // Guardar los cambios
        budgetAlertRepository.save(alert);
        log.info("Alerta {} marcada como leída", alertId);
    }

    // Marcar todas las alertas como leídas para un usuario
    @Override
    @Transactional
    public void markAllAlertsAsRead(Long userId) {
        // Buscar todas las alertas no leídas
        List<BudgetAlert> unreadAlerts = budgetAlertRepository.findByUserIdAndIsRead(userId, false);

        // Cambiar el estado de todas a "leída"
        unreadAlerts.forEach(alert -> alert.setIsRead(true));

        // Guardar todos los cambios en lote
        budgetAlertRepository.saveAll(unreadAlerts);

        log.info("Todas las alertas marcadas como leídas para el usuario: {}", userId);
    }

    // Contar cuántas alertas no leídas tiene un usuario
    @Override
    public long getUnreadAlertsCount(Long userId) {
        // Cuenta el número de alertas con isRead = false para el usuario
        return budgetAlertRepository.countByUserIdAndIsRead(userId, false);
    }

    // Método auxiliar: convierte una entidad BudgetAlert en un DTO
    private BudgetAlertDTO mapToDTO(BudgetAlert alert) {
        // Obtener el nombre del presupuesto asociado (si existe)
        String budgetName = budgetRepository.findById(alert.getBudgetId())
                .map(Budget::getName)
                .orElse("Presupuesto desconocido");

        // Construir el DTO de respuesta con los datos de la alerta
        return new BudgetAlertDTO(
                alert.getId(),
                alert.getBudgetId(),
                budgetName,
                alert.getType(),
                alert.getPercentageUsed(),
                alert.getAlertDate(),
                alert.getIsRead(),
                alert.getMessage()
        );
    }
}