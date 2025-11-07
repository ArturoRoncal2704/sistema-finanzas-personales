package com.arturo.transactionservice.service.impl;

import com.arturo.transactionservice.dto.request.TransactionRequest;
import com.arturo.transactionservice.dto.response.BalanceResponse;
import com.arturo.transactionservice.dto.response.CategoryDTO;
import com.arturo.transactionservice.dto.response.TransactionResponse;
import com.arturo.transactionservice.entity.Category;
import com.arturo.transactionservice.entity.Transaction;
import com.arturo.transactionservice.enums.TransactionType;
import com.arturo.transactionservice.exception.BadRequestException;
import com.arturo.transactionservice.exception.ResourceNotFoundException;
import com.arturo.transactionservice.repository.CategoryRepository;
import com.arturo.transactionservice.repository.TransactionRepository;
import com.arturo.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    //maneja las consultas sobre la tabla de transacciones
    private final CategoryRepository categoryRepository;
    //Permite obtener información de las categorías
    
    @Override
    @Transactional
    //Crea una nuestra transacción
    public TransactionResponse createTransaction(TransactionRequest request, Long userId) {
        log.info("Creación de transacciones para el usuario: {}", userId);
        
        // Validar categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        
        // Validar que el tipo de transacción coincida con el tipo de categoría
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("El tipo de categoría no coincide con el tipo de transacción.");
        }
        
        // Crear transacción
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setCategory(category);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setIsRecurring(request.getIsRecurring());
        transaction.setRecurringFrequency(request.getRecurringFrequency());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transacción creada correctamente con el ID: {}", savedTransaction.getId());
        
        return mapToResponse(savedTransaction);
    }
    
    @Override
    @Transactional
    // Actualiza una transacción existente
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, Long userId) {
        log.info("Actualizando transacción {} Para el usuario: {}", id, userId);
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        
        // Verificar que la transacción pertenece al usuario
        if (!transaction.getUserId().equals(userId)) {
            throw new BadRequestException("La transacción no pertenece al usuario");
        }
        
        // Validar categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("El tipo de categoría no coincide con el tipo de transacción.");
        }
        
        // Actualizar campos
        transaction.setCategory(category);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setIsRecurring(request.getIsRecurring());
        transaction.setRecurringFrequency(request.getRecurringFrequency());
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transacción actualizada exitosamente");
        
        return mapToResponse(updatedTransaction);
    }
    
    @Override
    //Elimina una transacción
    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        log.info("Eliminar transacción {} Para el usuario: {}", id, userId);
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        
        if (!transaction.getUserId().equals(userId)) {
            throw new BadRequestException("La transacción no pertenece al usuario");
        }
        
        transactionRepository.delete(transaction);
        log.info("Transacción eliminada exitosamente");
    }
    
    @Override
    //Busca una transacción por su ID, pero solo si pertenece al usuario actual
    public TransactionResponse getTransactionById(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        
        if (!transaction.getUserId().equals(userId)) {
            throw new BadRequestException("La transacción no pertenece al usuario");
        }
        
        return mapToResponse(transaction);
    }
    
    @Override
    //Devuelve todas las transacciones del usuario
    public Page<TransactionResponse> getAllTransactions(Long userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    //Filtra por tipo (INGRESO o GASTO)
    public Page<TransactionResponse> getTransactionsByType(Long userId, TransactionType type, 
                                                           Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndType(
                userId, type, pageable);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    //Filtra entre dos fechas(startDate - endDate)
    public Page<TransactionResponse> getTransactionsByDateRange(Long userId, LocalDate startDate, 
                                                                LocalDate endDate, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId, startDate, endDate, pageable);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    //Filtra por categoría.
    public Page<TransactionResponse> getTransactionsByCategory(Long userId, Long categoryId, 
                                                               Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                userId, categoryId, pageable);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    //Calcula el balance general del usuario en un rango de fechas
    public BalanceResponse getBalance(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Cálculo del saldo para el usuario {} de {} a {}", userId, startDate, endDate);

        //Calcular totales de ingresos y gastos
        BigDecimal totalIngresos = transactionRepository.calculateTotalByTypeAndDateRange(
                userId, TransactionType.INGRESO, startDate, endDate);
        
        BigDecimal totalGastos = transactionRepository.calculateTotalByTypeAndDateRange(
                userId, TransactionType.GASTO, startDate, endDate);

        //Calcular balance general
        BigDecimal balance = totalIngresos.subtract(totalGastos);
        
        // Calcular gastos por categoría
        List<Object[]> gastosData = transactionRepository.calculateTotalByCategory(
                userId, TransactionType.GASTO, startDate, endDate);
        
        Map<String, BigDecimal> gastosPorCategoria = new HashMap<>();
        for (Object[] data : gastosData) {
            gastosPorCategoria.put((String) data[0], (BigDecimal) data[1]);
        }
        
        // Calcular ingresos por categoría
        List<Object[]> ingresosData = transactionRepository.calculateTotalByCategory(
                userId, TransactionType.INGRESO, startDate, endDate);
        //Convertir esos resultados en mapas (Map<String, BigDecimal>):
        Map<String, BigDecimal> ingresosPorCategoria = new HashMap<>();

        for (Object[] data : ingresosData) {
            ingresosPorCategoria.put((String) data[0], (BigDecimal) data[1]);
        }
        //Devuelve un objeto BalanceResponse
        return new BalanceResponse(
                totalIngresos,
                totalGastos,
                balance,
                startDate,
                endDate,
                gastosPorCategoria,
                ingresosPorCategoria
        );
    }
    
    @Override
    //Calcula cuanto se ha gastado en una categoría específica(o en general)
    public BigDecimal calculateSpentAmount(Long userId, Long categoryId, LocalDate startDate, 
                                          LocalDate endDate) {
        if (categoryId == null) {
            // Si no hay categoría, calcular total de gastos
            return transactionRepository.calculateTotalByTypeAndDateRange(
                    userId, TransactionType.GASTO, startDate, endDate);
        }
        // Filtrar por categoría específica
        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                userId, categoryId, Pageable.unpaged());

        return transactions.stream()
                //Filtrar solo transacciones de tipo GASTO.
                .filter(t -> t.getType() == TransactionType.GASTO)
                //Verificar que estén dentro del rango de fechas.
                .filter(t -> !t.getTransactionDate().isBefore(startDate) && 
                            !t.getTransactionDate().isAfter(endDate))
                .map(Transaction::getAmount)
                //Sumar los montos con:
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        //Convierte una entidad Transaction en un DTO TransactionResponse para devolver al cliente.
        CategoryDTO categoryDTO = new CategoryDTO(
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCategory().getDescription(),
                transaction.getCategory().getType(),
                transaction.getCategory().getIconName(),
                transaction.getCategory().getColorHex(),
                transaction.getCategory().getIsDefault()
        );
        
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                categoryDTO,
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getReceiptUrl(),
                transaction.getIsRecurring(),
                transaction.getRecurringFrequency(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}