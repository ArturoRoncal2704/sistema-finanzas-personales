package com.arturo.budgetservice.controller;

import com.arturo.budgetservice.dto.response.BudgetAlertDTO;
import com.arturo.budgetservice.dto.response.MessageResponse;
import com.arturo.budgetservice.service.BudgetAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertController {

    // Servicio que maneja la lógica de negocio relacionada con las alertas de presupuesto
    private final BudgetAlertService budgetAlertService;

    // Obtener todas las alertas de un usuario (leídas y no leídas)
    @GetMapping // GET /alerts
    public ResponseEntity<List<BudgetAlertDTO>> getAllAlerts(
            @RequestHeader("X-User-Id") Long userId) { // ID del usuario autenticado en el header
        log.info("Obteniendo todas las alertas para el usuario: {}", userId);

        // Llama al servicio para recuperar todas las alertas
        List<BudgetAlertDTO> alerts = budgetAlertService.getAllAlerts(userId);

        // Devuelve la lista en la respuesta HTTP 200 (OK)
        return ResponseEntity.ok(alerts);
    }

    // Obtener solo las alertas no leídas
    @GetMapping("/unread") // GET /alerts/unread
    public ResponseEntity<List<BudgetAlertDTO>> getUnreadAlerts(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Obteniendo alertas no leídas para el usuario: {}", userId);

        // Llama al servicio para obtener solo las no leídas
        List<BudgetAlertDTO> alerts = budgetAlertService.getUnreadAlerts(userId);

        return ResponseEntity.ok(alerts);
    }

    // Obtener la cantidad de alertas no leídas
    @GetMapping("/unread/count") // GET /alerts/unread/count
    public ResponseEntity<Long> getUnreadAlertsCount(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Contando alertas no leídas para el usuario: {}", userId);

        // Llama al servicio para obtener el número total de alertas no leídas
        long count = budgetAlertService.getUnreadAlertsCount(userId);

        // Devuelve el conteo (por ejemplo: 5)
        return ResponseEntity.ok(count);
    }

    // Marcar una alerta específica como leída
    @PutMapping("/{id}/read") // PUT /alerts/{id}/read
    public ResponseEntity<MessageResponse> markAlertAsRead(
            @PathVariable Long id,                       // ID de la alerta
            @RequestHeader("X-User-Id") Long userId) {   // Usuario autenticado
        log.info("Marcando la alerta {} como leída para el usuario: {}", id, userId);

        // Llama al servicio para marcar la alerta como leída
        budgetAlertService.markAlertAsRead(id, userId);

        // Devuelve un mensaje de confirmación
        return ResponseEntity.ok(new MessageResponse("Alerta marcada como leída correctamente."));
    }

    // Marcar todas las alertas del usuario como leídas
    @PutMapping("/read-all") // PUT /alerts/read-all
    public ResponseEntity<MessageResponse> markAllAlertsAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Marcando todas las alertas como leídas para el usuario: {}", userId);

        // Llama al servicio para marcar todas como leídas
        budgetAlertService.markAllAlertsAsRead(userId);

        return ResponseEntity.ok(new MessageResponse("Todas las alertas fueron marcadas como leídas."));
    }
}
