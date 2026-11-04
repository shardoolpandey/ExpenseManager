package org.example.expensemanager.dto;

import org.example.expensemanager.model.Category;
import org.example.expensemanager.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, String description, BigDecimal amount, LocalDate date, Category category) {

    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getDescription(), e.getAmount(), e.getDate(), e.getCategory());
    }
}
