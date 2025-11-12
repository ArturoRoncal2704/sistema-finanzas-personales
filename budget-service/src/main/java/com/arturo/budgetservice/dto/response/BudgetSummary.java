package com.arturo.budgetservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummary {
    
    private Integer totalBudgets;
    private Integer activeBudgets;
    private Integer budgetsOnTrack;
    private Integer budgetsWithWarning;
    private Integer budgetsExceeded;
    private BigDecimal totalBudgeted;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
}