package com.arturo.transactionservice.dto.request;

import com.arturo.transactionservice.enums.RecurringFrequency;
import com.arturo.transactionservice.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Se requiere el tipo de transacción.")
    private TransactionType type;

    @NotNull(message = "Se requiere categoria")
    private Long categoryId;

    @NotNull(message = "Se requiere cantidad")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que 0.")
    private BigDecimal amount;

    @NotNull(message = "Se requiere la fecha de la transacción.")
    @PastOrPresent(message = "La fecha de la transacción no puede ser futura.")
    //Permite fechas pasadas o del día actual
    private LocalDate transactionDate;

    @Size(max = 500, message = "La descripción es demasiado larga.")
    private String description;

    private Boolean isRecurring = false;

    private RecurringFrequency recurringFrequency;
}
