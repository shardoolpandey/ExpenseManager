package org.example.expensemanager.repository;

import org.example.expensemanager.model.Category;
import org.example.expensemanager.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            select e from Expense e
            where (:category is null or e.category = :category)
              and (cast(:from as date) is null or e.date >= :from)
              and (cast(:to as date) is null or e.date <= :to)
            order by e.date desc, e.id desc
            """)
    List<Expense> search(@Param("category") Category category,
                         @Param("from") LocalDate from,
                         @Param("to") LocalDate to);

    @Query("""
            select e.category as category, sum(e.amount) as total from Expense e
            where e.date >= :from and e.date <= :to
            group by e.category
            """)
    List<CategoryTotal> totalsByCategory(@Param("from") LocalDate from, @Param("to") LocalDate to);

    interface CategoryTotal {
        Category getCategory();

        BigDecimal getTotal();
    }
}
