package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonData {

    private LocalDate period1StartDate;
    private LocalDate period1EndDate;
    private BigDecimal period1Ingresos;
    private BigDecimal period1Gastos;
    private BigDecimal period1Balance;

    private LocalDate period2StartDate;
    private LocalDate period2EndDate;
    private BigDecimal period2Ingresos;
    private BigDecimal period2Gastos;
    private BigDecimal period2Balance;

    private BigDecimal diferenciaIngresos;
    private BigDecimal diferenciaGastos;
    private BigDecimal diferenciaBalance;

    private BigDecimal porcentajeCambioIngresos;
    private BigDecimal porcentajeCambioGastos;
    private BigDecimal porcentajeCambioBalance;
}