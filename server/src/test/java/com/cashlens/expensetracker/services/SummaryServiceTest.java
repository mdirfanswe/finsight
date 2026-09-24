package com.cashlens.expensetracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cashlens.expensetracker.DTO.Summary.ExpenseSummary;
import com.cashlens.expensetracker.DTO.Summary.IncomeSummary;
import com.cashlens.expensetracker.DTO.Summary.Summary;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private IncomeService incomeService;

    @InjectMocks
    private SummaryService summaryService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId("user-1");
        user.setUsername("alice");
    }

    @Test
    void getSummary_shouldLoadUserAndDelegateBothSummaries() {
        ExpenseSummary expenseSummary = new ExpenseSummary(2L, null, List.of());
        IncomeSummary incomeSummary = new IncomeSummary(3L, null, List.of());
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseService.getExpenseSummary("user-1")).thenReturn(expenseSummary);
        when(incomeService.getIncomeSummary("user-1")).thenReturn(incomeSummary);

        Summary result = summaryService.getSummary("alice");

        assertSame(expenseSummary, result.getExpenseSummary());
        assertSame(incomeSummary, result.getIncomeSummary());
        verify(expenseService).getExpenseSummary("user-1");
        verify(incomeService).getIncomeSummary("user-1");
    }

    @Test
    void getSummary_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> summaryService.getSummary("alice"));
    }

    @Test
    void getLastNTransactions_shouldFetchBothTypesForTheUser() {
        List<ExpenseModel> expenses = List.of();
        List<IncomeModel> incomes = List.of();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseService.getLastNExpenses("user-1", 5)).thenReturn(expenses);
        when(incomeService.getLastNIncome("user-1", 5)).thenReturn(incomes);

        List<Object> result = summaryService.getLastNTransactions("alice", 5);

        assertEquals(0, result.size());
        verify(expenseService).getLastNExpenses("user-1", 5);
        verify(incomeService).getLastNIncome("user-1", 5);
    }

    @Test
    void getTransactionList_shouldMergeBothListsInDescendingDateOrder() {
        ExpenseModel expense1 = new ExpenseModel();
        expense1.setExpenseDate(LocalDate.of(2026, 9, 24));
        ExpenseModel expense2 = new ExpenseModel();
        expense2.setExpenseDate(LocalDate.of(2026, 9, 20));

        IncomeModel income1 = new IncomeModel();
        income1.setIncomeDate(LocalDate.of(2026, 9, 23));
        IncomeModel income2 = new IncomeModel();
        income2.setIncomeDate(LocalDate.of(2026, 9, 19));

        List<Object> result = summaryService.getTransactionList(
                List.of(expense1, expense2), List.of(income1, income2), 10);

        assertEquals(List.of(expense1, income1, expense2, income2), result);
    }

    @Test
    void getTransactionList_shouldPreferExpenseWhenDatesAreEqual() {
        ExpenseModel expense = new ExpenseModel();
        expense.setExpenseDate(LocalDate.of(2026, 9, 20));
        IncomeModel income = new IncomeModel();
        income.setIncomeDate(LocalDate.of(2026, 9, 20));

        List<Object> result = summaryService.getTransactionList(List.of(expense), List.of(income), 2);

        assertEquals(List.of(expense, income), result);
    }

    @Test
    void getTransactionList_shouldReturnAtMostRequestedCountAndHandleEmptySide() {
        ExpenseModel expense1 = new ExpenseModel();
        expense1.setExpenseDate(LocalDate.of(2026, 9, 24));
        ExpenseModel expense2 = new ExpenseModel();
        expense2.setExpenseDate(LocalDate.of(2026, 9, 23));

        List<Object> result = summaryService.getTransactionList(List.of(expense1, expense2), List.of(), 1);

        assertEquals(List.of(expense1), result);
    }
}
