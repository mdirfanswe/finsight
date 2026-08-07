package com.cashlens.expensetracker.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cashlens.expensetracker.DTO.Response.ExpenseListResponse;
import com.cashlens.expensetracker.DTO.Summary.ExpenseSummary;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.ExpenseRepository;

import static com.cashlens.expensetracker.utils.constants.PaginationConstants.PAGE_LIMIT;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private UserService userService;

        public ExpenseListResponse getAllExpenses(String username, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.DESC, "expenseDate"));
                Sort sort = Sort.by(Sort.Direction.DESC, "expenseDate");
                List<ExpenseModel> expenseList = expenseRepository.findByUser_Id(user.getId(), sort);

                return new ExpenseListResponse(expenseList, expenseList.size());
        }

        public ExpenseModel save(ExpenseModel expense, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                expense.setUser(user);
                if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
                        expense.setCategory("Miscellaneous");
                }
                if (expense.getIcon() == null || expense.getIcon().trim().isEmpty()) {
                        expense.setIcon("❓");
                }
                return expenseRepository.save(expense);
        }

        @Transactional
        public boolean delete(String expenseId, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                String userId = user.getId();
                long deletedRows = expenseRepository.deleteByUser_IdAndId(userId, expenseId);
                return deletedRows > 0;
        }

        public ExpenseModel update(ExpenseModel newExpense, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                ExpenseModel expenseInDB = expenseRepository.findById(newExpense.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Expense not found with id: " + newExpense.getId()));

                if (!expenseInDB.getUser().getId().equals(user.getId())) {
                        throw new IllegalArgumentException(
                                        "Unauthorized update of expense with id: " + newExpense.getId());
                }

                expenseInDB.setAmount(newExpense.getAmount());
                expenseInDB.setCategory(
                                newExpense.getCategory() == null || newExpense.getCategory().trim().isEmpty()
                                                ? "Miscellaneous"
                                                : newExpense.getCategory());
                expenseInDB.setIcon(
                                newExpense.getIcon() == null || newExpense.getIcon().trim().isEmpty() ? "❓"
                                                : newExpense.getIcon());
                expenseInDB.setDescription(newExpense.getDescription());
                expenseInDB.setExpenseDate(newExpense.getExpenseDate());
                expenseInDB.setPaymentMethod(newExpense.getPaymentMethod());

                return expenseRepository.save(expenseInDB);
        }

        public ExpenseListResponse getLastNDaysExpenses(String username, int n, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.DESC, "expenseDate"));
                Sort sort = Sort.by(Sort.Direction.DESC, "expenseDate");
                LocalDate startDate = LocalDate.now().minusDays(n);

                List<ExpenseModel> expenseList = expenseRepository.findLastNExpense(user.getId(), startDate, sort);

                return new ExpenseListResponse(expenseList, expenseList.size());
        }

        public ExpenseListResponse getExpensesByDate(String username, LocalDate date, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT);
                List<ExpenseModel> expenseList = expenseRepository.findExpenseOnDate(user.getId(), date);

                return new ExpenseListResponse(expenseList, expenseList.size());
        }

        public ExpenseListResponse getExpensesByMonth(String username, int month, int year, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.ASC, "expenseDate"));
                Sort sort = Sort.by(Sort.Direction.ASC, "expenseDate");
                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                List<ExpenseModel> expenseList = expenseRepository.findByUser_IdAndExpenseDateBetween(user.getId(),
                                startDate, endDate, sort);

                return new ExpenseListResponse(expenseList, expenseList.size());
        }

        public ExpenseListResponse getExpensesByYear(String username, int year, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.ASC, "expenseDate"));
                Sort sort = Sort.by(Sort.Direction.ASC, "expenseDate");
                LocalDate startDate = LocalDate.of(year, 1, 1);
                LocalDate endDate = LocalDate.of(year, 12, 31);

                List<ExpenseModel> expenseList = expenseRepository.findByUser_IdAndExpenseDateBetween(user.getId(),
                                startDate, endDate, sort);

                return new ExpenseListResponse(expenseList, expenseList.size());
        }

        public List<ExpenseModel> getLastNExpenses(String userId, int count) {
                Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC,
                                "expenseDate"));
                return expenseRepository.findByUser_Id(userId, pageable).getContent();
        }

        public ExpenseSummary getExpenseSummary(String userId) {
                Object[] stats = (Object[]) expenseRepository.getExpenseStats(userId);
                Long totalCount = (Long) stats[0];
                BigDecimal totalAmount = (BigDecimal) stats[1];

                List<ExpenseModel> expenseList = getLastNExpenses(userId, 10);

                return new ExpenseSummary(totalCount, totalAmount, expenseList);
        }
}
