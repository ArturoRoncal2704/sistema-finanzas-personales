package com.arturo.budgetservice.dto.request;

import com.arturo.budgetservice.enums.BudgetPeriod;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotBlank(message = "El nombre del presupuesto es obligatorio")
    @Size(max = 100, message = "El nombre es demasiado largo")
    private String name;

    private Long categoryId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1.00", message = "El monto debe ser al menos 1")
    private BigDecimal amount;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de finalización es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El período es obligatorio")
    private BudgetPeriod period;

    @DecimalMin(value = "0", message = "El umbral de alerta debe ser positivo")
    @DecimalMax(value = "100", message = "El umbral de alerta debe ser menor que 100")
    private BigDecimal alertThreshold;
}
