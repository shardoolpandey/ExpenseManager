package org.example.expensemanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.expensemanager.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String description,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
        @NotNull LocalDate date,
        @NotNull Category category) {
}
