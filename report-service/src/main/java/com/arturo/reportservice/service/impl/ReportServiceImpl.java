package com.arturo.reportservice.service.impl;

import com.arturo.reportservice.client.BudgetClient;
import com.arturo.reportservice.client.TransactionClient;
import com.arturo.reportservice.dto.BalanceDTO;
import com.arturo.reportservice.dto.BudgetSummaryDTO;
import com.arturo.reportservice.dto.PageResponse;
import com.arturo.reportservice.dto.TransactionDTO;
import com.arturo.reportservice.dto.response.*;
import com.arturo.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    // Clientes Feign para comunicar este microservicio con otros (microservicios externos)
    private final TransactionClient transactionClient; // Para obtener transacciones, balances, etc.
    private final BudgetClient budgetClient;           // Para obtener presupuestos y resúmenes

    // Método 1: Obtiene los datos del dashboard principal del usuario
    @Override
    public DashboardData getDashboardData(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Obteniendo datos del dashboard para el usuario {} del {} al {}", userId, startDate, endDate);

        DashboardData dashboard = new DashboardData(); // DTO que se enviará como respuesta

        // Obtener el balance general desde el transaction-service
        BalanceDTO balance = transactionClient.getBalance(userId, startDate, endDate);
        dashboard.setTotalIngresos(balance.getTotalIngresos());
        dashboard.setTotalGastos(balance.getTotalGastos());
        dashboard.setBalance(balance.getBalance());

        // Calcular ahorros (porcentaje del ingreso que no se gastó)
        dashboard.setAhorrosMes(balance.getBalance());
        if (balance.getTotalIngresos().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal porcentajeAhorro = balance.getBalance()
                    .divide(balance.getTotalIngresos(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dashboard.setPorcentajeAhorro(porcentajeAhorro);
        } else {
            dashboard.setPorcentajeAhorro(BigDecimal.ZERO);
        }

        // Gastos e ingresos por categoría
        dashboard.setGastosPorCategoria(balance.getGastosPorCategoria());
        dashboard.setIngresosPorCategoria(balance.getIngresosPorCategoria());

        // Determinar las 5 categorías con más gasto
        List<CategorySpending> topCategorias = balance.getGastosPorCategoria().entrySet().stream()
                .map(entry -> {
                    BigDecimal percentage = BigDecimal.ZERO;
                    if (balance.getTotalGastos().compareTo(BigDecimal.ZERO) > 0) {
                        percentage = entry.getValue()
                                .divide(balance.getTotalGastos(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                    }
                    return new CategorySpending(entry.getKey(), entry.getValue(), percentage, 0);
                })
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount())) // Orden descendente
                .limit(5) // Solo las 5 principales
                .collect(Collectors.toList());
        dashboard.setTopCategorias(topCategorias);

        // Resumen de presupuestos (consulta al budget-service)
        try {
            BudgetSummaryDTO budgetSummary = budgetClient.getBudgetSummary(userId);
            dashboard.setTotalPresupuestos(budgetSummary.getTotalBudgets());
            dashboard.setPresupuestosActivos(budgetSummary.getActiveBudgets());
            dashboard.setPresupuestosEnRiesgo(
                    budgetSummary.getBudgetsWithWarning() + budgetSummary.getBudgetsExceeded());
            dashboard.setTotalPresupuestado(budgetSummary.getTotalBudgeted());
            dashboard.setTotalGastado(budgetSummary.getTotalSpent());
        } catch (Exception e) {
            log.warn("Error al obtener el resumen de presupuestos: {}", e.getMessage());
            dashboard.setTotalPresupuestos(0);
            dashboard.setPresupuestosActivos(0);
            dashboard.setPresupuestosEnRiesgo(0);
            dashboard.setTotalPresupuestado(BigDecimal.ZERO);
            dashboard.setTotalGastado(BigDecimal.ZERO);
        }

        // Obtener las 5 transacciones más recientes
        PageResponse<TransactionDTO> recentTransactions = transactionClient.getAllTransactions(
                userId, 0, 5, "transactionDate", "DESC");
        dashboard.setTransaccionesRecientes(recentTransactions.getContent());

        // Obtener todas las transacciones del período para estadísticas generales
        PageResponse<TransactionDTO> allTransactions = transactionClient.getTransactionsByDateRange(
                userId, startDate, endDate, 0, 1000);
        dashboard.setTotalTransacciones((int) allTransactions.getTotalElements());

        // Calcular gasto promedio diario
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal gastoPromedioDiario = balance.getTotalGastos()
                .divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);
        dashboard.setGastoPromedioDiario(gastoPromedioDiario);

        // Categoría con mayor gasto total
        if (!balance.getGastosPorCategoria().isEmpty()) {
            String categoriaConMasGasto = balance.getGastosPorCategoria().entrySet().stream()
                    .max(Map.Entry.comparingByValue()) // Mayor valor
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            dashboard.setCategoriaConMasGasto(categoriaConMasGasto);
        } else {
            dashboard.setCategoriaConMasGasto("N/A");
        }

        log.info("Datos del dashboard generados correctamente");
        return dashboard;
    }

    // Método 2: Obtiene el resumen mensual (ingresos, gastos y balances)
    @Override
    public MonthlySummary getMonthlySummary(Long userId, int year, int month) {
        log.info("Obteniendo resumen mensual para el usuario {} - {}/{}", userId, year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        MonthlySummary summary = new MonthlySummary();
        summary.setYear(year);
        summary.setMonth(month);
        summary.setMonthName(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")));

        // Balance del mes desde transaction-service
        BalanceDTO balance = transactionClient.getBalance(userId, startDate, endDate);
        summary.setTotalIngresos(balance.getTotalIngresos());
        summary.setTotalGastos(balance.getTotalGastos());
        summary.setBalance(balance.getBalance());
        summary.setGastosPorCategoria(balance.getGastosPorCategoria());
        summary.setIngresosPorCategoria(balance.getIngresosPorCategoria());

        // Obtener todas las transacciones del mes
        PageResponse<TransactionDTO> transactions = transactionClient.getTransactionsByDateRange(
                userId, startDate, endDate, 0, 1000);
        List<TransactionDTO> allTransactions = transactions.getContent();
        summary.setCantidadTransacciones(allTransactions.size());

        // Contar ingresos y gastos
        long ingresoCount = allTransactions.stream().filter(t -> "INGRESO".equals(t.getType())).count();
        long gastoCount = allTransactions.stream().filter(t -> "GASTO".equals(t.getType())).count();
        summary.setCantidadIngresos((int) ingresoCount);
        summary.setCantidadGastos((int) gastoCount);

        // Promedio de gasto diario
        BigDecimal promedioGastoDiario = balance.getTotalGastos()
                .divide(BigDecimal.valueOf(yearMonth.lengthOfMonth()), 2, RoundingMode.HALF_UP);
        summary.setPromedioGastoDiario(promedioGastoDiario);

        // Categoría con más gasto
        if (!balance.getGastosPorCategoria().isEmpty()) {
            Map.Entry<String, BigDecimal> maxEntry = balance.getGastosPorCategoria().entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);
            if (maxEntry != null) {
                summary.setCategoriaConMasGasto(maxEntry.getKey());
                summary.setMontoMayorGasto(maxEntry.getValue());
            }
        }

        // Balance diario (ingresos/gastos por día)
        List<DailyBalance> balanceDiario = new ArrayList<>();
        Map<LocalDate, List<TransactionDTO>> transactionsByDate = allTransactions.stream()
                .collect(Collectors.groupingBy(TransactionDTO::getTransactionDate));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<TransactionDTO> dayTransactions = transactionsByDate.getOrDefault(date, Collections.emptyList());

            BigDecimal dayIngresos = dayTransactions.stream()
                    .filter(t -> "INGRESO".equals(t.getType()))
                    .map(TransactionDTO::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dayGastos = dayTransactions.stream()
                    .filter(t -> "GASTO".equals(t.getType()))
                    .map(TransactionDTO::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dayBalance = dayIngresos.subtract(dayGastos);

            balanceDiario.add(new DailyBalance(date, dayIngresos, dayGastos, dayBalance));
        }
        summary.setBalanceDiario(balanceDiario);

        log.info("Resumen mensual generado correctamente");
        return summary;
    }

    // Método 3: Analiza una categoría específica (por ejemplo: “Transporte”)
    @Override
    public CategoryAnalysis getCategoryAnalysis(Long userId, String categoryName,
                                                LocalDate startDate, LocalDate endDate) {
        log.info("Analizando categoría '{}' para el usuario {} entre {} y {}", categoryName, userId, startDate, endDate);

        CategoryAnalysis analysis = new CategoryAnalysis();
        analysis.setCategoryName(categoryName);

        // Obtener balance general
        BalanceDTO balance = transactionClient.getBalance(userId, startDate, endDate);

        // Gasto total de la categoría
        BigDecimal categorySpent = balance.getGastosPorCategoria()
                .getOrDefault(categoryName, BigDecimal.ZERO);
        analysis.setTotalSpent(categorySpent);

        // Porcentaje respecto al gasto total
        if (balance.getTotalGastos().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = categorySpent
                    .divide(balance.getTotalGastos(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            analysis.setPercentage(percentage);
        } else {
            analysis.setPercentage(BigDecimal.ZERO);
        }

        // Filtrar transacciones de esa categoría
        PageResponse<TransactionDTO> transactions = transactionClient.getTransactionsByDateRange(
                userId, startDate, endDate, 0, 1000);
        List<TransactionDTO> categoryTransactions = transactions.getContent().stream()
                .filter(t -> t.getCategory() != null &&
                        categoryName.equals(t.getCategory().getName()) &&
                        "GASTO".equals(t.getType()))
                .collect(Collectors.toList());
        analysis.setTransactionCount(categoryTransactions.size());

        // Promedio por transacción
        if (!categoryTransactions.isEmpty()) {
            BigDecimal average = categorySpent
                    .divide(BigDecimal.valueOf(categoryTransactions.size()), 2, RoundingMode.HALF_UP);
            analysis.setAverageTransaction(average);
        } else {
            analysis.setAverageTransaction(BigDecimal.ZERO);
        }

        // Tendencia de los últimos 6 meses
        List<MonthlySpending> monthlyTrend = new ArrayList<>();
        LocalDate trendStartDate = endDate.minusMonths(5).withDayOfMonth(1);

        for (int i = 0; i < 6; i++) {
            YearMonth ym = YearMonth.from(trendStartDate.plusMonths(i));
            LocalDate monthStart = ym.atDay(1);
            LocalDate monthEnd = ym.atEndOfMonth();

            BalanceDTO monthBalance = transactionClient.getBalance(userId, monthStart, monthEnd);
            BigDecimal monthAmount = monthBalance.getGastosPorCategoria()
                    .getOrDefault(categoryName, BigDecimal.ZERO);

            monthlyTrend.add(new MonthlySpending(
                    ym.getYear(),
                    ym.getMonthValue(),
                    ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")),
                    monthAmount
            ));
        }
        analysis.setMonthlyTrend(monthlyTrend);

        // Por ahora no se incluye información de presupuesto
        analysis.setBudgetAmount(BigDecimal.ZERO);
        analysis.setBudgetRemaining(BigDecimal.ZERO);
        analysis.setBudgetPercentageUsed(BigDecimal.ZERO);

        log.info("Análisis de categoría generado correctamente");
        return analysis;
    }

    // Método 4: Compara dos periodos distintos (por ejemplo, enero vs febrero)
    @Override
    public ComparisonData comparePeriods(Long userId,
                                         LocalDate period1Start, LocalDate period1End,
                                         LocalDate period2Start, LocalDate period2End) {
        log.info("Comparando periodos para el usuario {}: [{} a {}] vs [{} a {}]",
                userId, period1Start, period1End, period2Start, period2End);

        ComparisonData comparison = new ComparisonData();

        // Período 1
        BalanceDTO balance1 = transactionClient.getBalance(userId, period1Start, period1End);
        comparison.setPeriod1StartDate(period1Start);
        comparison.setPeriod1EndDate(period1End);
        comparison.setPeriod1Ingresos(balance1.getTotalIngresos());
        comparison.setPeriod1Gastos(balance1.getTotalGastos());
        comparison.setPeriod1Balance(balance1.getBalance());

        // Período 2
        BalanceDTO balance2 = transactionClient.getBalance(userId, period2Start, period2End);
        comparison.setPeriod2StartDate(period2Start);
        comparison.setPeriod2EndDate(period2End);
        comparison.setPeriod2Ingresos(balance2.getTotalIngresos());
        comparison.setPeriod2Gastos(balance2.getTotalGastos());
        comparison.setPeriod2Balance(balance2.getBalance());

        // Diferencias absolutas
        comparison.setDiferenciaIngresos(balance2.getTotalIngresos().subtract(balance1.getTotalIngresos()));
        comparison.setDiferenciaGastos(balance2.getTotalGastos().subtract(balance1.getTotalGastos()));
        comparison.setDiferenciaBalance(balance2.getBalance().subtract(balance1.getBalance()));

        // Porcentajes de cambio
        comparison.setPorcentajeCambioIngresos(
                calculatePercentageChange(balance1.getTotalIngresos(), balance2.getTotalIngresos()));
        comparison.setPorcentajeCambioGastos(
                calculatePercentageChange(balance1.getTotalGastos(), balance2.getTotalGastos()));
        comparison.setPorcentajeCambioBalance(
                calculatePercentageChange(balance1.getBalance(), balance2.getBalance()));

        log.info("Comparación de periodos generada correctamente");
        return comparison;
    }

    // Método auxiliar para calcular el cambio porcentual entre dos valores
    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return newValue.subtract(oldValue)
                .divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}