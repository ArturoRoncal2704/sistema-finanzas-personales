package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysis {
    
    private String categoryName;
    private BigDecimal totalSpent;
    private BigDecimal percentage;
    private Integer transactionCount;
    private BigDecimal averageTransaction;
    
    private BigDecimal budgetAmount;
    private BigDecimal budgetRemaining;
    private BigDecimal budgetPercentageUsed;
    
    private List<MonthlySpending> monthlyTrend;
}