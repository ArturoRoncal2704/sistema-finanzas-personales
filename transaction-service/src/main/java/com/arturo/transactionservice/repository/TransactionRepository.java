package com.arturo.transactionservice.repository;

import com.arturo.transactionservice.entity.Transaction;
import com.arturo.transactionservice.enums.TransactionType;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //buscar por usuario
    //Pageable permite paginar y ordenar resultados, ejemplo las 10 transacciones más recienters de un usuario
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    List<Transaction> findByUserId(Long userId);

    //Buscar por usuario y tipo
    Page<Transaction> findByUserIdAndType(
            Long userId,
            TransactionType type,
            Pageable pageable);

    //Buscar por usuario y rango de fechas
    Page<Transaction> findByUserIdAndTransactionDateBetween
            (Long userId,
             LocalDate startDate,
             LocalDate endDate,
             Pageable pageable);

    //Buscar por usuario, tipo y rango de fechas
    Page<Transaction> findByUserIdAndTypeAndTransactionDateBetween(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    //Calcular total por tipo en un rango de fechas
    //Calcula la suma total(SUM) de montos(amount)
    //De un tipo de transacción
    //Detnro de un rango de fechas
    //COALESCE -> devuelve 0 si no hay resultados (evita null)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            //Calcula la suma del campo amount de la entidad Transaction
            //Si no hay resultados(es decir,SUM devuelve null), usa 0 con COALESCE
            "WHERE t.userId = :userId " +
            //Filtra solo las transacciones del usuario que pasas como parametro
            "AND t.type = :type " +
            //Filtra por tipo
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
            //Solo cuenta las transacciones dentro del rango de fechas indicado
    BigDecimal calculateTotalByTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Calcular total por categoría en un rango de fechas
    //Agrupa las transacciones por categoría (GROUP BY)
    //Devuelve una lista de arreglos Object[] donde
    //[0] -> nombre de la categoría
    //[1] -> Total acumulado
    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
            //Selecciona el nombre de la categoría y la suima total de los montos, usa la entidad transacciones
            "WHERE t.userId = :userId " +
            //Filtra las transacciones de un usuario específico
            "AND t.type = :type " +
            //Filtra por tipo
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            //Solo toma en cuenta las transacciones dentro del rango de fechas
            "GROUP BY t.category.name")
            //Agrupa las transacciones por nombre de categoría
    List<Object[]> calculateTotalByCategory(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Buscar por usuario y categoría
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    // Contar transacciones por usuario
    long countByUserId(Long userId);
}
