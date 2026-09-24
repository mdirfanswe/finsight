package com.cashlens.expensetracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cashlens.expensetracker.DTO.Response.ExpenseListResponse;
import com.cashlens.expensetracker.DTO.Summary.ExpenseSummary;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId("user-1");
        user.setUsername("alice");
    }

    @Test
    void save_shouldAttachUserAndApplyDefaultCategoryAndIcon() {
        ExpenseModel expense = new ExpenseModel();
        expense.setCategory("  ");
        expense.setIcon("");
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.save(expense)).thenReturn(expense);

        ExpenseModel result = expenseService.save(expense, "alice");

        assertSame(expense, result);
        assertSame(user, expense.getUser());
        assertEquals("Miscellaneous", expense.getCategory());
        assertEquals("❓", expense.getIcon());
        verify(expenseRepository).save(expense);
    }

    @Test
    void save_shouldRejectUnknownUser() {
        ExpenseModel expense = new ExpenseModel();
        when(userService.getByUsername("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> expenseService.save(expense, "alice"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void delete_shouldReturnTrueWhenRowWasDeleted() {
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.deleteByUser_IdAndId("user-1", "expense-1")).thenReturn(1L);

        assertEquals(true, expenseService.delete("expense-1", "alice"));
        verify(expenseRepository).deleteByUser_IdAndId("user-1", "expense-1");
    }

    @Test
    void delete_shouldReturnFalseWhenNoRowWasDeleted() {
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.deleteByUser_IdAndId("user-1", "missing")).thenReturn(0L);

        assertEquals(false, expenseService.delete("missing", "alice"));
    }

    @Test
    void update_shouldThrowWhenExpenseDoesNotExist() {
        ExpenseModel update = new ExpenseModel();
        update.setId("missing");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> expenseService.update(update, "alice"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void update_shouldRejectExpenseOwnedByAnotherUser() {
        ExpenseModel stored = new ExpenseModel();
        stored.setId("expense-1");
        UserModel otherUser = new UserModel();
        otherUser.setId("user-2");
        stored.setUser(otherUser);

        ExpenseModel update = new ExpenseModel();
        update.setId("expense-1");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.findById("expense-1")).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> expenseService.update(update, "alice"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void update_shouldApplyDefaultsAndPersistChangedFields() {
        ExpenseModel stored = new ExpenseModel();
        stored.setId("expense-1");
        stored.setUser(user);

        ExpenseModel update = new ExpenseModel();
        update.setId("expense-1");
        update.setAmount(new BigDecimal("125.50"));
        update.setCategory(" ");
        update.setIcon(null);
        update.setDescription("Updated");
        update.setExpenseDate(LocalDate.of(2026, 9, 20));
        update.setPaymentMethod("UPI");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.findById("expense-1")).thenReturn(Optional.of(stored));
        when(expenseRepository.save(stored)).thenReturn(stored);

        ExpenseModel result = expenseService.update(update, "alice");

        assertSame(stored, result);
        assertEquals(new BigDecimal("125.50"), stored.getAmount());
        assertEquals("Miscellaneous", stored.getCategory());
        assertEquals("❓", stored.getIcon());
        assertEquals("Updated", stored.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), stored.getExpenseDate());
        assertEquals("UPI", stored.getPaymentMethod());
        verify(expenseRepository).save(stored);
    }

    @Test
    void getExpensesByMonth_shouldUseFirstAndLastDayOfMonth() {
        List<ExpenseModel> expenses = List.of(new ExpenseModel());
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser_IdAndExpenseDateBetween(
                "user-1", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
                Sort.by(Sort.Direction.ASC, "expenseDate"))).thenReturn(expenses);

        ExpenseListResponse response = expenseService.getExpensesByMonth("alice", 2, 2026, 0);

        assertSame(expenses, response.getExpenseList());
        assertEquals(1, response.getTotalCount());
        verify(expenseRepository).findByUser_IdAndExpenseDateBetween(
                "user-1", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
                Sort.by(Sort.Direction.ASC, "expenseDate"));
    }

    @Test
    void getExpensesByYear_shouldUseFullCalendarYear() {
        List<ExpenseModel> expenses = List.of();
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser_IdAndExpenseDateBetween(
                "user-1", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                Sort.by(Sort.Direction.ASC, "expenseDate"))).thenReturn(expenses);

        ExpenseListResponse response = expenseService.getExpensesByYear("alice", 2026, 4);

        assertSame(expenses, response.getExpenseList());
        assertEquals(0, response.getTotalCount());
    }

    @Test
    void getLastNExpenses_shouldBuildDescendingPageRequestWithRequestedCount() {
        when(expenseRepository.findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new ExpenseModel())));

        List<ExpenseModel> result = expenseService.getLastNExpenses("user-1", 5);

        assertEquals(1, result.size());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(expenseRepository).findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                captor.capture());
        assertEquals(5, captor.getValue().getPageSize());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(Sort.Direction.DESC, captor.getValue().getSort().getOrderFor("expenseDate").getDirection());
    }

    @Test
    void getExpenseSummary_shouldCombineRepositoryStatsAndLastTenExpenses() {
        List<ExpenseModel> lastTen = List.of(new ExpenseModel(), new ExpenseModel());
        when(expenseRepository.getExpenseStats("user-1"))
                .thenReturn(new Object[] { 7L, new BigDecimal("1450.75") });
        when(expenseRepository.findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(lastTen));

        ExpenseSummary summary = expenseService.getExpenseSummary("user-1");

        assertEquals(7L, summary.getTotalCount());
        assertEquals(new BigDecimal("1450.75"), summary.getTotalAmount());
        assertEquals(lastTen, summary.getExpenseList());
    }
}
