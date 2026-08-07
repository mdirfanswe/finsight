package com.cashlens.expensetracker.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cashlens.expensetracker.models.IncomeModel;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeModel, String> {
    Page<IncomeModel> findByUser_Id(String id, Pageable pageable);

    List<IncomeModel> findByUser_Id(String id, Sort sort);

    Long deleteByUser_IdAndId(String userId, String incomeId);

    @Query("SELECT e FROM Income e WHERE e.user.id = :userId AND e.incomeDate >= :startDate")
    List<IncomeModel> findLastNIncome(String userId, LocalDate startDate, Sort sort);

    @Query("SELECT e FROM Income e WHERE e.user.id = :userId AND e.incomeDate = :date")
    List<IncomeModel> findIncomeOnDate(String userId, LocalDate date);

    List<IncomeModel> findByUser_IdAndIncomeDateBetween(String userId, LocalDate startDate, LocalDate endDate,
            Sort sort);

    @Query("SELECT COUNT(e) as totalCount, SUM(e.amount) as totalAmount FROM Income e WHERE e.user.id = :userId")
    Object getIncomeStats(String userId);
}