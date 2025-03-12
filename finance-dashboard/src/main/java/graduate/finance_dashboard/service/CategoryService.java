package graduate.finance_dashboard.service;

import graduate.finance_dashboard.exception.ApiException;
import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.User;
import graduate.finance_dashboard.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(Category category, User user) {
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUser(User user) {
        return categoryRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException("Category not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails, User user) {
        Category category = getCategoryById(id);
        if (!category.getUser().getId().equals(user.getId())) {
            throw new ApiException("You don't have permission to update this category", HttpStatus.FORBIDDEN);
        }
        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id, User user) {
        Category category = getCategoryById(id);
        if (!category.getUser().getId().equals(user.getId())) {
            throw new ApiException("You don't have permission to delete this category", HttpStatus.FORBIDDEN);
        }
        categoryRepository.deleteById(id);
    }
}
