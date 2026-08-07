package com.cashlens.expensetracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cashlens.expensetracker.DTO.Summary.Summary;
import com.cashlens.expensetracker.services.SummaryService;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private SummaryService summaryService;

    @GetMapping()
    public Summary getSummary() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return summaryService.getSummary(username);
    }

    @GetMapping("/last-n-transactions")
    public List<Object> getLastNTransactions(@RequestParam("count") int count) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return summaryService.getLastNTransactions(username, count);
    }

}
