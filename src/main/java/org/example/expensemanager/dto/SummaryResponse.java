package org.example.expensemanager.dto;

import org.example.expensemanager.model.Category;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public record SummaryResponse(YearMonth month, BigDecimal total, Map<Category, BigDecimal> byCategory) {
}
