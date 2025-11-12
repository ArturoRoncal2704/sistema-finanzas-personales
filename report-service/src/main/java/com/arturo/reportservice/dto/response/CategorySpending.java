package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpending {
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Integer transactionCount;
}