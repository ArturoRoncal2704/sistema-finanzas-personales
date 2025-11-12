package com.arturo.budgetservice.dto.response;

import com.arturo.budgetservice.dto.CategoryDTO;
import com.arturo.budgetservice.enums.BudgetPeriod;
import com.arturo.budgetservice.enums.BudgetStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    
    private Long id;
    private Long userId;
    private String name;
    private CategoryDTO category;
    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private BigDecimal percentageUsed;
    private LocalDate startDate;
    private LocalDate endDate;
    private BudgetPeriod period;
    private BudgetStatus status;
    private BigDecimal alertThreshold;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}