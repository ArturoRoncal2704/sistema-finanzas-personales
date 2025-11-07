package com.arturo.budgetservice.entity;

import com.arturo.budgetservice.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_alerts", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_is_read", columnList = "is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "budget_id", nullable = false)
    private Long budgetId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;
    
    @Column(name = "percentage_used", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentageUsed;
    
    @Column(name = "alert_date", nullable = false)
    private LocalDateTime alertDate;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(length = 500)
    private String message;
    
    @PrePersist
    protected void onCreate() {
        if (alertDate == null) {
            alertDate = LocalDateTime.now();
        }
    }
}