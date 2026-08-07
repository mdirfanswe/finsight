package com.cashlens.expensetracker.DTO.Response;

import java.util.List;

import com.cashlens.expensetracker.models.ExpenseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExpenseListResponse {
    private List<ExpenseModel> expenseList;
    private long totalCount;
}
