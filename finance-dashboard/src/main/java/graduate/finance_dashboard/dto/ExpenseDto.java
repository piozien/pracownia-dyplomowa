package graduate.finance_dashboard.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private LocalDateTime date;
}
