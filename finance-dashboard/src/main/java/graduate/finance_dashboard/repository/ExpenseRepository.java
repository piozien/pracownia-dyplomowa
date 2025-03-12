package graduate.finance_dashboard.repository;

import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.Expense;
import graduate.finance_dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserAndCategory(User user, Category category);
    List<Expense> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
