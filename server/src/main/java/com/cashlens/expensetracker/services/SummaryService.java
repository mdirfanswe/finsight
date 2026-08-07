package com.cashlens.expensetracker.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cashlens.expensetracker.DTO.Summary.Summary;
import com.cashlens.expensetracker.DTO.Summary.ExpenseSummary;
import com.cashlens.expensetracker.DTO.Summary.IncomeSummary;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.UserRepository;

@Service
public class SummaryService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private IncomeService incomeService;

    public Summary getSummary(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ExpenseSummary expenseSummary = expenseService.getExpenseSummary(user.getId());
        IncomeSummary incomeSummary = incomeService.getIncomeSummary(user.getId());
        return new Summary(expenseSummary, incomeSummary);
    }

    public List<Object> getLastNTransactions(String username, int count) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<ExpenseModel> expenseList = expenseService.getLastNExpenses(user.getId(), count);
        List<IncomeModel> incomeList = incomeService.getLastNIncome(user.getId(), count);

        return getTransactionList(expenseList, incomeList, count);
    }

    public List<Object> getTransactionList(List<ExpenseModel> expenseList, List<IncomeModel> incomeList, int count) {
        int expensePointer = 0, incomePointer = 0;

        List<Object> transactionList = new ArrayList<>();
        while (expensePointer < expenseList.size() && incomePointer < incomeList.size() && count > 0) {
            ExpenseModel expense = expenseList.get(expensePointer);
            IncomeModel income = incomeList.get(incomePointer);
            if (expense.getExpenseDate().compareTo(income.getIncomeDate()) >= 0) {
                transactionList.add(expense);
                expensePointer++;
            } else {
                transactionList.add(income);
                incomePointer++;
            }
            count--;
        }

        while (expensePointer < expenseList.size() && count > 0) {
            transactionList.add(expenseList.get(expensePointer));
            expensePointer++;
            count--;
        }

        while (incomePointer < incomeList.size() && count > 0) {
            transactionList.add(incomeList.get(incomePointer));
            count--;
        }

        return transactionList;
    }
}
