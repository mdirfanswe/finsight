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

import com.cashlens.expensetracker.DTO.Response.IncomeListResponse;
import com.cashlens.expensetracker.DTO.Summary.IncomeSummary;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.IncomeRepository;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IncomeService incomeService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId("user-1");
        user.setUsername("alice");
    }

    @Test
    void save_shouldAttachUserAndApplyDefaultCategoryAndIcon() {
        IncomeModel income = new IncomeModel();
        income.setCategory(null);
        income.setIcon("   ");
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.save(income)).thenReturn(income);

        IncomeModel result = incomeService.save(income, "alice");

        assertSame(income, result);
        assertSame(user, income.getUser());
        assertEquals("Miscellaneous", income.getCategory());
        assertEquals("❓", income.getIcon());
        verify(incomeRepository).save(income);
    }

    @Test
    void save_shouldRejectUnknownUser() {
        IncomeModel income = new IncomeModel();
        when(userService.getByUsername("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> incomeService.save(income, "alice"));
        verify(incomeRepository, never()).save(any());
    }

    @Test
    void delete_shouldReturnFalseWhenNoRowWasDeleted() {
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.deleteByUser_IdAndId("user-1", "missing")).thenReturn(0L);

        assertEquals(false, incomeService.delete("missing", "alice"));
    }

    @Test
    void update_shouldThrowWhenIncomeDoesNotExist() {
        IncomeModel update = new IncomeModel();
        update.setId("missing");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> incomeService.update(update, "alice"));
        verify(incomeRepository, never()).save(any());
    }

    @Test
    void update_shouldRejectIncomeOwnedByAnotherUser() {
        IncomeModel stored = new IncomeModel();
        stored.setId("income-1");
        UserModel otherUser = new UserModel();
        otherUser.setId("user-2");
        stored.setUser(otherUser);

        IncomeModel update = new IncomeModel();
        update.setId("income-1");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.findById("income-1")).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> incomeService.update(update, "alice"));
        verify(incomeRepository, never()).save(any());
    }

    @Test
    void update_shouldApplyDefaultsAndPersistChangedFields() {
        IncomeModel stored = new IncomeModel();
        stored.setId("income-1");
        stored.setUser(user);

        IncomeModel update = new IncomeModel();
        update.setId("income-1");
        update.setAmount(new BigDecimal("900.00"));
        update.setCategory("");
        update.setIcon(" ");
        update.setDescription("Salary");
        update.setIncomeDate(LocalDate.of(2026, 9, 15));
        update.setPaymentMethod("Bank Transfer");

        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.findById("income-1")).thenReturn(Optional.of(stored));
        when(incomeRepository.save(stored)).thenReturn(stored);

        IncomeModel result = incomeService.update(update, "alice");

        assertSame(stored, result);
        assertEquals(new BigDecimal("900.00"), stored.getAmount());
        assertEquals("Miscellaneous", stored.getCategory());
        assertEquals("❓", stored.getIcon());
        assertEquals("Salary", stored.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), stored.getIncomeDate());
        assertEquals("Bank Transfer", stored.getPaymentMethod());
        verify(incomeRepository).save(stored);
    }

    @Test
    void getIncomeByMonth_shouldUseCorrectMonthBounds() {
        List<IncomeModel> incomes = List.of(new IncomeModel());
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.findByUser_IdAndIncomeDateBetween(
                "user-1", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
                Sort.by(Sort.Direction.ASC, "incomeDate"))).thenReturn(incomes);

        IncomeListResponse response = incomeService.getIncomeByMonth("alice", 2, 2026, 0);

        assertSame(incomes, response.getIncomeList());
        assertEquals(1, response.getTotalCount());
    }

    @Test
    void getIncomeByDate_shouldReturnRepositoryResultsAndCount() {
        LocalDate date = LocalDate.of(2026, 9, 24);
        List<IncomeModel> incomes = List.of(new IncomeModel(), new IncomeModel());
        when(userService.getByUsername("alice")).thenReturn(Optional.of(user));
        when(incomeRepository.findIncomeOnDate("user-1", date)).thenReturn(incomes);

        IncomeListResponse response = incomeService.getIncomeByDate("alice", date, 2);

        assertSame(incomes, response.getIncomeList());
        assertEquals(2, response.getTotalCount());
        verify(incomeRepository).findIncomeOnDate("user-1", date);
    }

    @Test
    void getLastNIncome_shouldUseRequestedCountAndDescendingDateSort() {
        when(incomeRepository.findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new IncomeModel())));

        List<IncomeModel> result = incomeService.getLastNIncome("user-1", 3);

        assertEquals(1, result.size());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(incomeRepository).findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                captor.capture());
        assertEquals(3, captor.getValue().getPageSize());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(Sort.Direction.DESC,
                captor.getValue().getSort().getOrderFor("incomeDate").getDirection());
    }
    @Test
    void getIncomeSummary_shouldCombineStatsAndLastTenIncomes() {
        List<IncomeModel> lastTen = List.of(new IncomeModel(), new IncomeModel());

        when(incomeRepository.getIncomeStats("user-1"))
                .thenReturn(new Object[] { 4L, new BigDecimal("3100.00") });

        when(incomeRepository.findByUser_Id(
                org.mockito.ArgumentMatchers.eq("user-1"),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(lastTen));

        IncomeSummary summary = incomeService.getIncomeSummary("user-1");

        assertEquals(4L, summary.getTotalCount());
        assertEquals(new BigDecimal("3100.00"), summary.getTotalAmount());
        assertEquals(lastTen, summary.getIncomeList());
    }
}
