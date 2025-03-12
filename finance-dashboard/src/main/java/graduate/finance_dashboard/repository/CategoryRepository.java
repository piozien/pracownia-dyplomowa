package graduate.finance_dashboard.repository;

import graduate.finance_dashboard.model.Category;
import graduate.finance_dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
}
