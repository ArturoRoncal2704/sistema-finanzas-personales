package com.arturo.reportservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, BigDecimal> gastosPorCategoria;
    private Map<String, BigDecimal> ingresosPorCategoria;
}