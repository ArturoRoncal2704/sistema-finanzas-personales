package com.arturo.transactionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, BigDecimal> gastosPorCategoria;
    private Map<String, BigDecimal> ingresosPorCategoria;
}