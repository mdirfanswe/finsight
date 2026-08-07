package com.cashlens.expensetracker.DTO.Summary;

import java.math.BigDecimal;
import java.util.List;

import com.cashlens.expensetracker.models.ExpenseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExpenseSummary {
    private Long totalCount;
    private BigDecimal totalAmount;
    private List<ExpenseModel> expenseList;
}
