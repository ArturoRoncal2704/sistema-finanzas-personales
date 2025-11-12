package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySpending {
    private Integer year;
    private Integer month;
    private String monthName;
    private BigDecimal amount;
}