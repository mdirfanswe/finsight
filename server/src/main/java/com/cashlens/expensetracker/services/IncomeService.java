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
import com.cashlens.expensetracker.DTO.Response.IncomeListResponse;
import com.cashlens.expensetracker.DTO.Summary.IncomeSummary;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.IncomeRepository;

import static com.cashlens.expensetracker.utils.constants.PaginationConstants.PAGE_LIMIT;

import jakarta.transaction.Transactional;

@Service
public class IncomeService {
        @Autowired
        private IncomeRepository incomeRepository;

        @Autowired
        private UserService userService;

        public IncomeListResponse getAllIncome(String username, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.DESC, "incomeDate"));
                Sort sort = Sort.by(Sort.Direction.DESC, "incomeDate");
                List<IncomeModel> incomeList = incomeRepository.findByUser_Id(user.getId(), sort);

                return new IncomeListResponse(incomeList, incomeList.size());
        }

        public IncomeModel save(IncomeModel income, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                income.setUser(user);
                if (income.getCategory() == null || income.getCategory().trim().isEmpty()) {
                        income.setCategory("Miscellaneous");
                }
                if (income.getIcon() == null || income.getIcon().trim().isEmpty()) {
                        income.setIcon("❓");
                }
                return incomeRepository.save(income);
        }

        @Transactional
        public boolean delete(String incomeId, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                String userId = user.getId();
                long deletedRows = incomeRepository.deleteByUser_IdAndId(userId, incomeId);
                return deletedRows > 0;
        }

        public IncomeModel update(IncomeModel newIncome, String username) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                IncomeModel incomeInDB = incomeRepository.findById(newIncome.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Expense not found with id: " + newIncome.getId()));

                if (!incomeInDB.getUser().getId().equals(user.getId())) {
                        throw new IllegalArgumentException(
                                        "Unauthorized update of expense with id: " + newIncome.getId());
                }

                incomeInDB.setAmount(newIncome.getAmount());
                incomeInDB.setCategory(
                                newIncome.getCategory() == null || newIncome.getCategory().trim().isEmpty()
                                                ? "Miscellaneous"
                                                : newIncome.getCategory());
                incomeInDB.setIcon(
                                newIncome.getIcon() == null || newIncome.getIcon().trim().isEmpty() ? "❓"
                                                : newIncome.getIcon());
                incomeInDB.setDescription(newIncome.getDescription());
                incomeInDB.setIncomeDate(newIncome.getIncomeDate());
                incomeInDB.setPaymentMethod(newIncome.getPaymentMethod());

                return incomeRepository.save(incomeInDB);
        }

        public IncomeListResponse getLastNDaysIncome(String username, int n, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.DESC, "incomeDate"));
                Sort sort = Sort.by(Sort.Direction.DESC, "incomeDate");
                LocalDate startDate = LocalDate.now().minusDays(n);

                List<IncomeModel> incomeList = incomeRepository.findLastNIncome(user.getId(), startDate, sort);

                return new IncomeListResponse(incomeList, incomeList.size());
        }

        public IncomeListResponse getIncomeByDate(String username, LocalDate date, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT);
                List<IncomeModel> incomeList = incomeRepository.findIncomeOnDate(user.getId(), date);

                return new IncomeListResponse(incomeList, incomeList.size());
        }

        public IncomeListResponse getIncomeByMonth(String username, int month, int year, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.ASC, "incomeDate"));
                Sort sort = Sort.by(Sort.Direction.ASC, "incomeDate");
                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                List<IncomeModel> incomeList = incomeRepository.findByUser_IdAndIncomeDateBetween(user.getId(),
                                startDate, endDate, sort);

                return new IncomeListResponse(incomeList, incomeList.size());
        }

        public IncomeListResponse getIncomeByYear(String username, int year, int pageNo) {
                UserModel user = userService.getByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));

                // Pageable pageable = PageRequest.of(pageNo, PAGE_LIMIT,
                // Sort.by(Sort.Direction.ASC, "incomeDate"));
                Sort sort = Sort.by(Sort.Direction.ASC, "incomeDate");
                LocalDate startDate = LocalDate.of(year, 1, 1);
                LocalDate endDate = LocalDate.of(year, 12, 31);

                List<IncomeModel> incomeList = incomeRepository.findByUser_IdAndIncomeDateBetween(user.getId(),
                                startDate, endDate, sort);

                return new IncomeListResponse(incomeList, incomeList.size());
        }

        public List<IncomeModel> getLastNIncome(String userId, int count) {
                Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC,
                                "incomeDate"));
                return incomeRepository.findByUser_Id(userId, pageable).getContent();
        }

        public IncomeSummary getIncomeSummary(String userId) {
                Object[] stats = (Object[]) incomeRepository.getIncomeStats(userId);
                Long totalCount = (Long) stats[0];
                BigDecimal totalAmount = (BigDecimal) stats[1];

                List<IncomeModel> incomeList = getLastNIncome(userId, 10);

                return new IncomeSummary(totalCount, totalAmount, incomeList);
        }
}
