package graduate.finance_dashboard.controller;

import graduate.finance_dashboard.dto.ExpenseDto;
import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.Expense;
import graduate.finance_dashboard.model.User;
import graduate.finance_dashboard.service.CategoryService;
import graduate.finance_dashboard.service.ExpenseService;
import graduate.finance_dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final CategoryService categoryService;

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setCategoryId(expense.getCategory().getId());
        dto.setDate(expense.getCreatedAt());
        return dto;
    }

    private Expense convertToEntity(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            expense.setCategory(category);
        }
        return expense;
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@RequestBody ExpenseDto expenseDto, @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        Expense expense = convertToEntity(expenseDto);
        Expense createdExpense = expenseService.createExpense(expense, user);
        return ResponseEntity.ok(convertToDto(createdExpense));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getUserExpenses(@RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByUser(user);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ExpenseDto>> getExpensesByCategory(
            @PathVariable Long categoryId,
            @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        Category category = categoryService.getCategoryById(categoryId);
        List<Expense> expenses = expenseService.getExpensesByUserAndCategory(user, category);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseDto>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByUserAndDateRange(user, start, end);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpense(@PathVariable Long id, @RequestHeader("Username") String username) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(convertToDto(expense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseDto expenseDto,
            @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        Expense expense = convertToEntity(expenseDto);
        Expense updatedExpense = expenseService.updateExpense(id, expense, user);
        return ResponseEntity.ok(convertToDto(updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
    }
}
