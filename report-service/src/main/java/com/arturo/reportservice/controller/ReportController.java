package com.arturo.reportservice.controller;

import com.arturo.reportservice.dto.response.CategoryAnalysis;
import com.arturo.reportservice.dto.response.ComparisonData;
import com.arturo.reportservice.dto.response.DashboardData;
import com.arturo.reportservice.dto.response.MonthlySummary;
import com.arturo.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    // Inyección del servicio que maneja la lógica de generación de reportes
    private final ReportService reportService;

    // Obtener datos del panel principal (dashboard)
    @GetMapping("/dashboard") // GET /reports/dashboard
    public ResponseEntity<DashboardData> getDashboardData(
            @RequestHeader("X-User-Id") Long userId, // Se obtiene el ID del usuario desde el encabezado HTTP
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // Fecha de inicio
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) { // Fecha de fin
        log.info("Obteniendo datos del panel para el usuario {} del {} al {}", userId, startDate, endDate);

        // Llama al servicio para obtener los datos del dashboard (ingresos, gastos, balance, etc.)
        DashboardData data = reportService.getDashboardData(userId, startDate, endDate);

        // Devuelve los datos en la respuesta HTTP con estado 200 (OK)
        return ResponseEntity.ok(data);
    }

    // Obtener el resumen mensual (ingresos/gastos por mes)
    @GetMapping("/monthly-summary") // GET /reports/monthly-summary
    public ResponseEntity<MonthlySummary> getMonthlySummary(
            @RequestHeader("X-User-Id") Long userId, // Usuario autenticado
            @RequestParam int year,                  // Año del resumen
            @RequestParam int month) {               // Mes del resumen
        log.info("Obteniendo resumen mensual para el usuario {} - {}/{}", userId, year, month);

        // Llama al servicio para generar el resumen del mes especificado
        MonthlySummary summary = reportService.getMonthlySummary(userId, year, month);

        return ResponseEntity.ok(summary);
    }

    // Obtener un análisis detallado por categoría
    @GetMapping("/category-analysis") // GET /reports/category-analysis
    public ResponseEntity<CategoryAnalysis> getCategoryAnalysis(
            @RequestHeader("X-User-Id") Long userId, // Usuario autenticado
            @RequestParam String categoryName,        // Nombre de la categoría (ej: “Transporte”)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // Fecha inicio
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) { // Fecha fin
        log.info("Obteniendo análisis de la categoría '{}' para el usuario {}", categoryName, userId);

        // Llama al servicio para obtener los datos analíticos de esa categoría
        CategoryAnalysis analysis = reportService.getCategoryAnalysis(userId, categoryName, startDate, endDate);

        return ResponseEntity.ok(analysis);
    }

    // Comparar dos periodos distintos de tiempo (por ejemplo, comparar dos meses)
    @GetMapping("/comparison") // GET /reports/comparison
    public ResponseEntity<ComparisonData> comparePeriods(
            @RequestHeader("X-User-Id") Long userId, // Usuario autenticado
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start, //  Inicio periodo 1
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,   // Fin periodo 1
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start, // Inicio periodo 2
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) { //  Fin periodo 2
        log.info("Comparando periodos para el usuario {}", userId);

        // Llama al servicio para generar la comparación entre los dos periodos
        ComparisonData comparison = reportService.comparePeriods(
                userId, period1Start, period1End, period2Start, period2End);

        return ResponseEntity.ok(comparison);
    }
}