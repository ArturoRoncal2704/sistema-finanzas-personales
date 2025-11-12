package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {
    
    private Integer year;
    private Integer month;
    private String monthName;
    
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;
    private BigDecimal promedioGastoDiario;
    
    private Integer cantidadTransacciones;
    private Integer cantidadIngresos;
    private Integer cantidadGastos;
    
    private Map<String, BigDecimal> gastosPorCategoria;
    private Map<String, BigDecimal> ingresosPorCategoria;
    
    private String categoriaConMasGasto;
    private BigDecimal montoMayorGasto;
    
    private List<DailyBalance> balanceDiario;
}