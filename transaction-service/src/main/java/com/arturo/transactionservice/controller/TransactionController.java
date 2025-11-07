package com.arturo.transactionservice.controller;

import com.arturo.transactionservice.dto.request.TransactionRequest;
import com.arturo.transactionservice.dto.response.BalanceResponse;
import com.arturo.transactionservice.dto.response.MessageResponse;
import com.arturo.transactionservice.dto.response.TransactionResponse;
import com.arturo.transactionservice.enums.TransactionType;
import com.arturo.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    //Crear transaccion
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @RequestHeader("X-User-Id") Long userId) { // ID del usuario desde los headers (autenticación)
        log.info("Crear solicitud de transacción para el usuario\n: {}", userId);
        TransactionResponse response = transactionService.createTransaction(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);// 201 Created
    }
    
    @PutMapping("/{id}")
    //Actualizar transacción
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,//ID de la transacción a actualizar
            @Valid @RequestBody TransactionRequest request,
            @RequestHeader("X-User-Id") Long userId) {// ID del usuario desde los headers (autenticación)
        log.info("Actualizar transacción {} Para el usuario: {}", id, userId);
        TransactionResponse response = transactionService.updateTransaction(id, request, userId);
        return ResponseEntity.ok(response);// 200 OK
    }
    
    @DeleteMapping("/{id}")
    //Eliminar transacción
    public ResponseEntity<MessageResponse> deleteTransaction(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Eliminar transacción {} Para el usuario: {}", id, userId);
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.ok(new MessageResponse("Transacción eliminada exitosamente"));
    }
    
    @GetMapping("/{id}")
    //Obtener una transacción por ID
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Obtener transacción {} Para el usuario: {}", id, userId);
        TransactionResponse response = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    //Obtener todas las transacciones(paginadas y ordenadas)
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page, //Página actual
            @RequestParam(defaultValue = "10") int size,//Cantidad por página
            @RequestParam(defaultValue = "transactionDate") String sortBy,//Campo de orden
            @RequestParam(defaultValue = "DESC") String sortDir) { //Dirección del orden
        log.info("Obtener todas las transacciones para el usuario: {}", userId);
        // Crea el objeto Pageable con orden dinámico
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionResponse> transactions = transactionService.getAllTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/type/{type}")
    //Obtener por tipo(ingreso/gasto)
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByType(
            @PathVariable TransactionType type,// Enum: INGRESO o GASTO
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Obtener transacciones por tipo {} Para el usuario: {}", type, userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionResponse> transactions = transactionService.getTransactionsByType(
                userId, type, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    //Obtener por rangos de fechas
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByDateRange(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,// Fecha inicio
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,// Fecha fin
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Obtener transacciones para el usuario {} de {} a {}", userId, startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(
                userId, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/category/{categoryId}")
    //Obtener por categoría
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByCategory(
            @PathVariable Long categoryId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Obtener transacciones por categoría {} para el usuario: {}", categoryId, userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionResponse> transactions = transactionService.getTransactionsByCategory(
                userId, categoryId, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/balance")
    //calcular balance(ingresos-gastos)
    public ResponseEntity<BalanceResponse> getBalance(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Obtener saldo para el usuario {} de {} a {}", userId, startDate, endDate);
        
        BalanceResponse balance = transactionService.getBalance(userId, startDate, endDate);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/calculate-spent")
    //Calcular total gastado(opcional por categoría)
    public ResponseEntity<BigDecimal> calculateSpentAmount(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Calcular la cantidad gastada para el usuario {} de {} a {}", userId, startDate, endDate);
        
        BigDecimal spent = transactionService.calculateSpentAmount(userId, categoryId, startDate, endDate);
        return ResponseEntity.ok(spent);
    }
}