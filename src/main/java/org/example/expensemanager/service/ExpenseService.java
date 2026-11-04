package org.example.expensemanager.service;

import lombok.RequiredArgsConstructor;
import org.example.expensemanager.dto.ExpenseRequest;
import org.example.expensemanager.dto.ExpenseResponse;
import org.example.expensemanager.dto.SummaryResponse;
import org.example.expensemanager.exception.ExpenseNotFoundException;
import org.example.expensemanager.model.Category;
import org.example.expensemanager.model.Expense;
import org.example.expensemanager.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseResponse create(ExpenseRequest request) {
        return ExpenseResponse.from(repository.save(apply(new Expense(), request)));
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(Long id) {
        return ExpenseResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(Category category, LocalDate from, LocalDate to) {
        return repository.search(category, from, to).stream().map(ExpenseResponse::from).toList();
    }

    public ExpenseResponse update(Long id, ExpenseRequest request) {
        return ExpenseResponse.from(apply(find(id), request));
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(YearMonth month) {
        Map<Category, BigDecimal> byCategory = new EnumMap<>(Category.class);
        repository.totalsByCategory(month.atDay(1), month.atEndOfMonth())
                .forEach(t -> byCategory.put(t.getCategory(), t.getTotal()));
        BigDecimal total = byCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SummaryResponse(month, total, byCategory);
    }

    private Expense find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private Expense apply(Expense expense, ExpenseRequest request) {
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setCategory(request.category());
        return expense;
    }
}
