package com.arturo.transactionservice.service;

import com.arturo.transactionservice.dto.request.TransactionRequest;
import com.arturo.transactionservice.dto.response.BalanceResponse;
import com.arturo.transactionservice.dto.response.TransactionResponse;
import com.arturo.transactionservice.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionService {
    
    TransactionResponse createTransaction(TransactionRequest request, Long userId);
    
    TransactionResponse updateTransaction(Long id, TransactionRequest request, Long userId);
    
    void deleteTransaction(Long id, Long userId);
    
    TransactionResponse getTransactionById(Long id, Long userId);
    
    Page<TransactionResponse> getAllTransactions(Long userId, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByType(Long userId, TransactionType type, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByDateRange(Long userId, LocalDate startDate, 
                                                         LocalDate endDate, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByCategory(Long userId, Long categoryId, Pageable pageable);
    
    BalanceResponse getBalance(Long userId, LocalDate startDate, LocalDate endDate);
    
    BigDecimal calculateSpentAmount(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
}