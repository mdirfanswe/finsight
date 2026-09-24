package com.cashlens.expensetracker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cashlens.expensetracker.DTO.Response.IncomeListResponse;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.services.IncomeService;

@ExtendWith(MockitoExtension.class)
class IncomeControllerTest {

    @Mock
    private IncomeService incomeService;

    @InjectMocks
    private IncomeController incomeController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getIncomeList_shouldPrioritizeLastNDaysFilter() {
        authenticate("alice");
        IncomeListResponse expected = new IncomeListResponse(List.of(), 0);
        when(incomeService.getLastNDaysIncome("alice", 5, 0)).thenReturn(expected);

        var response = incomeController.getIncomeList(5, LocalDate.of(2026, 9, 24), YearMonth.of(2026, 9), 2026, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(incomeService).getLastNDaysIncome("alice", 5, 0);
    }

    @Test
    void getIncomeList_shouldUseDateWhenLastNDaysIsMissingOrNonPositive() {
        authenticate("alice");
        LocalDate date = LocalDate.of(2026, 9, 24);
        IncomeListResponse expected = new IncomeListResponse(List.of(), 0);
        when(incomeService.getIncomeByDate("alice", date, 2)).thenReturn(expected);

        var response = incomeController.getIncomeList(-1, date, null, null, 3);

        assertEquals(expected, response.getBody());
        verify(incomeService).getIncomeByDate("alice", date, 2);
    }

    @Test
    void getIncomeList_shouldUseMonthWhenDateIsMissing() {
        authenticate("alice");
        IncomeListResponse expected = new IncomeListResponse(List.of(), 0);
        when(incomeService.getIncomeByMonth("alice", 2, 2026, 1)).thenReturn(expected);

        var response = incomeController.getIncomeList(null, null, YearMonth.of(2026, 2), null, 2);

        assertEquals(expected, response.getBody());
    }

    @Test
    void getIncomeList_shouldUseYearWhenOnlyYearIsProvided() {
        authenticate("alice");
        IncomeListResponse expected = new IncomeListResponse(List.of(), 0);
        when(incomeService.getIncomeByYear("alice", 2025, 0)).thenReturn(expected);

        var response = incomeController.getIncomeList(null, null, null, 2025, 1);

        assertEquals(expected, response.getBody());
    }

    @Test
    void getIncomeList_shouldUseAllIncomeWhenNoFilterIsProvided() {
        authenticate("alice");
        IncomeListResponse expected = new IncomeListResponse(List.of(new IncomeModel()), 1);
        when(incomeService.getAllIncome("alice", 0)).thenReturn(expected);

        var response = incomeController.getIncomeList(null, null, null, null, 1);

        assertEquals(expected, response.getBody());
    }

    private void authenticate(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null));
    }
}
