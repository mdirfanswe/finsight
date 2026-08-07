package com.cashlens.expensetracker.DTO.Response;

import java.util.List;

import com.cashlens.expensetracker.models.IncomeModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class IncomeListResponse {
    private List<IncomeModel> incomeList;
    private long totalCount;
}
