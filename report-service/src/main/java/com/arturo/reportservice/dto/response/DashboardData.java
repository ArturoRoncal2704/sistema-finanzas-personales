package com.arturo.reportservice.dto.response;

import com.arturo.reportservice.dto.TransactionDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    
    // Resumen financiero
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;
    private BigDecimal ahorrosMes;
    private BigDecimal porcentajeAhorro;
    
    // Categorías
    private Map<String, BigDecimal> gastosPorCategoria;
    private Map<String, BigDecimal> ingresosPorCategoria;
    private List<CategorySpending> topCategorias;
    
    // Presupuestos
    private Integer totalPresupuestos;
    private Integer presupuestosActivos;
    private Integer presupuestosEnRiesgo;
    private BigDecimal totalPresupuestado;
    private BigDecimal totalGastado;
    
    // Transacciones recientes
    private List<TransactionDTO> transaccionesRecientes;
    
    // Estadísticas
    private Integer totalTransacciones;
    private BigDecimal gastoPromedioDiario;
    private String categoriaConMasGasto;
}