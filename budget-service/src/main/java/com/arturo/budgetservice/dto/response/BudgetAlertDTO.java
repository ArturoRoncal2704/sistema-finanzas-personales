package com.arturo.budgetservice.dto.response;

import com.arturo.budgetservice.enums.AlertType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertDTO {
    
    private Long id;
    private Long budgetId;
    private String budgetName;
    private AlertType type;
    private BigDecimal percentageUsed;
    private LocalDateTime alertDate;
    private Boolean isRead;
    private String message;
}