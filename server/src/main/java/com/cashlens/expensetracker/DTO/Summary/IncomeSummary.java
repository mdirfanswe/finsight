package com.cashlens.expensetracker.DTO.Summary;

import java.math.BigDecimal;
import java.util.List;

import com.cashlens.expensetracker.models.IncomeModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class IncomeSummary {
    private Long totalCount;
    private BigDecimal totalAmount;
    private List<IncomeModel> incomeList;
}
