package com.arturo.budgetservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;


@FeignClient(name = "transaction-service") // Cliente Feign: invoca al servicio registrado como "transaction-service" (Eureka o spring.application.name del remoto)
public interface TransactionClient {

    @GetMapping("/transactions/calculate-spent") // Realiza un GET al endpoint remoto /transactions/calculate-spent
    BigDecimal calculateSpentAmount(
            @RequestHeader("X-User-Id") Long userId, // Header HTTP requerido por el servicio remoto para identificar al usuario
            @RequestParam(required = false) Long categoryId, // Query param opcional: si es null, no se envía
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // Query param obligatorio: formateado como YYYY-MM-DD
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate    // Query param obligatorio: formateado como YYYY-MM-DD
    );
}