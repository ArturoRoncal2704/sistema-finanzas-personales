package com.arturo.transactionservice.entity;

import com.arturo.transactionservice.enums.RecurringFrequency;
import com.arturo.transactionservice.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity //Indica que esta clase se mapeará a una tabla de base de datos
@Table(name = "transactions", indexes = { //Define el nombre de la tabla "transactions" y agrega indices para optimizar consultas
        @Index(name = "idx_user_id", columnList = "user_id"), //Crea indicies en columnas usadas frecuentemente en busquedas, para mejorar rendimiendo
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_user_date", columnList = "user_id, transaction_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY) // La categoría solo se carga cuando se necesita, no de inmediato(mejora rendimiendo)
    //Relacion muchas transacciones -> una categoria
    @JoinColumn(name = "category_id",nullable = false) //Indica la columna category_id en la tabla
    private Category category;

    //Se guarda como texto no como número
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    //Precisión exacta para dinero, permite hasta 10 digitos en total, con 2 decimales
    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date" , nullable = false)
    private LocalDate transactionDate;

    @Column(length = 500)
    private String description;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_frequency")
    private RecurringFrequency recurringFrequency;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
