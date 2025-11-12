package com.arturo.budgetservice.repository;

import com.arturo.budgetservice.entity.Budget;
import com.arturo.budgetservice.enums.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository //Marca la interfaz como un componente de repositorio de Spring
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    //Extiende JpaRepositroy -> hereda metodos CRUD

    List<Budget> findByUserId(Long userId);
    //Busca todos los presupuestos que pertenecen a un usuario

    List<Budget> findByUserIdAndIsActive(Long userId, Boolean isActive);
    //Busca presupuestos de un usuario que estén activos o inactivos según el parámetro

    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);
    //Busca presupuestos de un usuario según el período (MENSUAL,ANUAL)

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    //Busca un presupuesto específico por su ID, verificando además que pertefezca al usuario indicado


    @Query("SELECT b FROM Budget b WHERE b.userId = :userId " +
           "AND b.startDate <= :date AND b.endDate >= :date " +
           "AND b.isActive = true")
    List<Budget> findActiveBudgetsForDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    //Busca todos los presupuestos activos de un usuario que estén vigentes en una fecha especifica
    //Ejm: si hoy es 2025-11-07 y tienes un presupuesto del 1 al 30 de noviembre, se devuelve
    //Se cumple:
    // startDAte <- date <- endDate
    //y además isActive = true

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId " +
           "AND b.categoryId = :categoryId " +
           "AND b.startDate <= :date AND b.endDate >= :date " +
           "AND b.isActive = true")
    Optional<Budget> findActiveBudgetForCategoryAndDate(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("date") LocalDate date
    );
    //Busca el presupuesto activo de un usuario para una categoría específica en una fecha dada
    //Ideal cuando un gasto ocurre el 2025-11-10 y quieres saber qué presupuesto está vigente
    //para esa categoría(ejmplo: comida) en ese día

    long countByUserId(Long userId);
    //Cuanta cuántos presupuestos tiene un usuario(activos o no)

    long countByUserIdAndIsActive(Long userId, Boolean isActive);
    //Cuenta cuántos presupuestos activos(o inactivos) tiene un usuario
}