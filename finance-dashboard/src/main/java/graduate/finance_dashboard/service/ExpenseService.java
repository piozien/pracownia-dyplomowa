package graduate.finance_dashboard.service;

import graduate.finance_dashboard.exception.ApiException;
import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.Expense;
import graduate.finance_dashboard.model.User;
import graduate.finance_dashboard.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public Expense createExpense(Expense expense, User user) {
        if (expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new ApiException("Amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        if (expense.getCategory() == null) {
            throw new ApiException("Category is required", HttpStatus.BAD_REQUEST);
        }
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUserAndCategory(User user, Category category) {
        return expenseRepository.findByUserAndCategory(user, category);
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUserAndDateRange(User user, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ApiException("Start and end dates are required", HttpStatus.BAD_REQUEST);
        }
        if (start.isAfter(end)) {
            throw new ApiException("Start date must be before end date", HttpStatus.BAD_REQUEST);
        }
        return expenseRepository.findByUserAndCreatedAtBetween(user, start, end);
    }

    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ApiException("Expense not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Expense updateExpense(Long id, Expense expenseDetails, User user) {
        Expense expense = getExpenseById(id);
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ApiException("You don't have permission to update this expense", HttpStatus.FORBIDDEN);
        }
        if (expenseDetails.getAmount() != null && expenseDetails.getAmount().signum() <= 0) {
            throw new ApiException("Amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        
        if (expenseDetails.getAmount() != null) {
            expense.setAmount(expenseDetails.getAmount());
        }
        if (expenseDetails.getDescription() != null) {
            expense.setDescription(expenseDetails.getDescription());
        }
        if (expenseDetails.getCategory() != null) {
            expense.setCategory(expenseDetails.getCategory());
        }
        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id, User user) {
        Expense expense = getExpenseById(id);
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ApiException("You don't have permission to delete this expense", HttpStatus.FORBIDDEN);
        }
        expenseRepository.deleteById(id);
    }
}
