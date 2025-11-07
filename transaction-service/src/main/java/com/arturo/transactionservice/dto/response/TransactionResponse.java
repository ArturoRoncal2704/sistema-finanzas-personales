package com.arturo.transactionservice.dto.response;

import com.arturo.transactionservice.enums.RecurringFrequency;
import com.arturo.transactionservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long userId;
    private CategoryDTO category;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String receiptUrl;
    private Boolean isRecurring;
    private RecurringFrequency recurringFrequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
