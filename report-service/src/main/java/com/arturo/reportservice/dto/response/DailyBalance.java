package com.arturo.reportservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyBalance {
    private LocalDate date;
    private BigDecimal ingresos;
    private BigDecimal gastos;
    private BigDecimal balance;
}