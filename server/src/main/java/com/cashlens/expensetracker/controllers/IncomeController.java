package com.cashlens.expensetracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cashlens.expensetracker.DTO.Response.IncomeListResponse;
import com.cashlens.expensetracker.models.IncomeModel;
import com.cashlens.expensetracker.services.IncomeService;

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
@RequestMapping("/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping()
    public ResponseEntity<IncomeListResponse> getIncomeList(
            @RequestParam(value = "lastNDays", required = false) Integer lastNDays,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "month", required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        page -= 1; // page starts from 0 in java

        if (lastNDays != null && lastNDays > 0) {
            return new ResponseEntity<>(incomeService.getLastNDaysIncome(username, lastNDays, page), HttpStatus.OK);
        } else if (date != null) {
            return new ResponseEntity<>(incomeService.getIncomeByDate(username, date, page), HttpStatus.OK);
        } else if (yearMonth != null) {
            int m = yearMonth.getMonthValue();
            int y = yearMonth.getYear();
            return new ResponseEntity<>(incomeService.getIncomeByMonth(username, m, y, page), HttpStatus.OK);
        } else if (year != null) {
            return new ResponseEntity<>(incomeService.getIncomeByYear(username, year, page), HttpStatus.OK);
        }

        return new ResponseEntity<>(incomeService.getAllIncome(username, page), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<IncomeModel> createExpenseEntry(@RequestBody IncomeModel income) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(incomeService.save(income, username), HttpStatus.CREATED);
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<String> deleteExpenseEntry(@PathVariable("incomeId") String incomeId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isIncomeDeleted = incomeService.delete(incomeId, username);
        if (isIncomeDeleted) {
            return new ResponseEntity<>("Entry deleted successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("No entry found!", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<IncomeModel> updateExpenseEntry(@PathVariable("incomeId") String incomeId,
            @RequestBody IncomeModel newIncome) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        newIncome.setId(incomeId);
        return new ResponseEntity<>(incomeService.update(newIncome, username), HttpStatus.OK);
    }
}
