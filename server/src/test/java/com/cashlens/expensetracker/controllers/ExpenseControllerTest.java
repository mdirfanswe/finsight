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

import com.cashlens.expensetracker.DTO.Response.ExpenseListResponse;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.services.ExpenseService;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getExpenseList_shouldPrioritizeLastNDaysFilter() {
        authenticate("alice");
        ExpenseListResponse expected = new ExpenseListResponse(List.of(), 0);
        when(expenseService.getLastNDaysExpenses("alice", 7, 0)).thenReturn(expected);

        var response = expenseController.getExpenseList(7, LocalDate.of(2026, 9, 24), YearMonth.of(2026, 9), 2026, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(expenseService).getLastNDaysExpenses("alice", 7, 0);
    }

    @Test
    void getExpenseList_shouldUseDateWhenLastNDaysIsMissingOrNonPositive() {
        authenticate("alice");
        LocalDate date = LocalDate.of(2026, 9, 24);
        ExpenseListResponse expected = new ExpenseListResponse(List.of(), 0);
        when(expenseService.getExpensesByDate("alice", date, 2)).thenReturn(expected);

        var response = expenseController.getExpenseList(0, date, null, null, 3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(expenseService).getExpensesByDate("alice", date, 2);
    }

    @Test
    void getExpenseList_shouldUseMonthWhenDateIsMissing() {
        authenticate("alice");
        YearMonth month = YearMonth.of(2026, 2);
        ExpenseListResponse expected = new ExpenseListResponse(List.of(), 0);
        when(expenseService.getExpensesByMonth("alice", 2, 2026, 1)).thenReturn(expected);

        var response = expenseController.getExpenseList(null, null, month, null, 2);

        assertEquals(expected, response.getBody());
    }

    @Test
    void getExpenseList_shouldUseYearWhenOnlyYearIsProvided() {
        authenticate("alice");
        ExpenseListResponse expected = new ExpenseListResponse(List.of(), 0);
        when(expenseService.getExpensesByYear("alice", 2025, 0)).thenReturn(expected);

        var response = expenseController.getExpenseList(null, null, null, 2025, 1);

        assertEquals(expected, response.getBody());
    }

    @Test
    void getExpenseList_shouldUseAllExpensesWhenNoFilterIsProvided() {
        authenticate("alice");
        ExpenseListResponse expected = new ExpenseListResponse(List.of(new ExpenseModel()), 1);
        when(expenseService.getAllExpenses("alice", 0)).thenReturn(expected);

        var response = expenseController.getExpenseList(null, null, null, null, 1);

        assertEquals(expected, response.getBody());
    }

    private void authenticate(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null));
    }
}
