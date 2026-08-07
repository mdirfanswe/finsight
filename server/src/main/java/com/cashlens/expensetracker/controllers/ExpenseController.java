package com.cashlens.expensetracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cashlens.expensetracker.DTO.Response.ExpenseListResponse;
import com.cashlens.expensetracker.models.ExpenseModel;
import com.cashlens.expensetracker.services.ExpenseService;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping()
    public ResponseEntity<ExpenseListResponse> getExpenseList(
            @RequestParam(value = "lastNDays", required = false) Integer lastNDays,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "month", required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        pageNo -= 1; // page starts from 0 in java

        if (lastNDays != null && lastNDays > 0) {
            return new ResponseEntity<>(expenseService.getLastNDaysExpenses(username, lastNDays, pageNo),
                    HttpStatus.OK);
        } else if (date != null) {
            return new ResponseEntity<>(expenseService.getExpensesByDate(username, date, pageNo), HttpStatus.OK);
        } else if (yearMonth != null) {
            int m = yearMonth.getMonthValue();
            int y = yearMonth.getYear();
            return new ResponseEntity<>(expenseService.getExpensesByMonth(username, m, y, pageNo),
                    HttpStatus.OK);
        } else if (year != null) {
            return new ResponseEntity<>(expenseService.getExpensesByYear(username, year, pageNo), HttpStatus.OK);
        }

        return new ResponseEntity<>(expenseService.getAllExpenses(username, pageNo), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ExpenseModel> createExpenseEntry(@RequestBody ExpenseModel expense) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(expenseService.save(expense, username), HttpStatus.CREATED);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpenseEntry(@PathVariable("expenseId") String expenseId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isExpeseDeleted = expenseService.delete(expenseId, username);
        if (isExpeseDeleted) {
            return new ResponseEntity<>("Entry deleted successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("No entry found!", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseModel> updateExpenseEntry(@PathVariable("expenseId") String expenseId,
            @RequestBody ExpenseModel newExpense) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        newExpense.setId(expenseId);
        return new ResponseEntity<>(expenseService.update(newExpense, username), HttpStatus.OK);
    }
}
