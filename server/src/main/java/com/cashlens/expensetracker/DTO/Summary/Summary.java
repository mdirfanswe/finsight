package com.cashlens.expensetracker.DTO.Summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Summary {
    private ExpenseSummary expenseSummary;
    private IncomeSummary incomeSummary;
}
