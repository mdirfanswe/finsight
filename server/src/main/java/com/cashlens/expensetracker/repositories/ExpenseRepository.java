package com.cashlens.expensetracker.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cashlens.expensetracker.models.ExpenseModel;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseModel, String> {
    Page<ExpenseModel> findByUser_Id(String id, Pageable pageable);

    List<ExpenseModel> findByUser_Id(String id, Sort sort);

    Long deleteByUser_IdAndId(String userId, String expenseId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.expenseDate >= :startDate")
    List<ExpenseModel> findLastNExpense(String userId, LocalDate startDate, Sort sort);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.expenseDate = :date")
    List<ExpenseModel> findExpenseOnDate(String userId, LocalDate date);

    List<ExpenseModel> findByUser_IdAndExpenseDateBetween(String userId, LocalDate startDate, LocalDate endDate,
            Sort sort);

    @Query("SELECT COUNT(e) as totalCount, SUM(e.amount) as totalAmount FROM Expense e WHERE e.user.id = :userId")
    Object getExpenseStats(String userId);
}