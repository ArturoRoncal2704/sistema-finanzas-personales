package com.arturo.reportservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long userId;
    private CategoryDTO category;
    private String type;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
}