package org.example.expensemanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expensemanager.dto.ExpenseRequest;
import org.example.expensemanager.dto.ExpenseResponse;
import org.example.expensemanager.dto.SummaryResponse;
import org.example.expensemanager.model.Category;
import org.example.expensemanager.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ExpenseResponse> list(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(category, from, to);
    }

    @GetMapping("/summary")
    public SummaryResponse summary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return service.summary(month);
    }

    @GetMapping("/{id}")
    public ExpenseResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
