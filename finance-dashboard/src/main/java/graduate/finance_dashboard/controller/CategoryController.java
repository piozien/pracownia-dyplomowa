package graduate.finance_dashboard.controller;

import graduate.finance_dashboard.dto.CategoryDto;
import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.User;
import graduate.finance_dashboard.service.CategoryService;
import graduate.finance_dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    private Category convertToEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto, @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        Category category = convertToEntity(categoryDto);
        Category createdCategory = categoryService.createCategory(category, user);
        return ResponseEntity.ok(convertToDto(createdCategory));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getUserCategories(@RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        List<Category> categories = categoryService.getCategoriesByUser(user);
        List<CategoryDto> categoryDtos = categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id, @RequestHeader("Username") String username) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(convertToDto(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto,
            @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        Category category = convertToEntity(categoryDto);
        Category updatedCategory = categoryService.updateCategory(id, category, user);
        return ResponseEntity.ok(convertToDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @RequestHeader("Username") String username) {
        User user = userService.getUserByUsername(username);
        categoryService.deleteCategory(id, user);
        return ResponseEntity.noContent().build();
    }
}
