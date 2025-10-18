package io.nghlong3004.penny.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Expense {
    private Long id;
    private Long userId;
    private Double amount;
    private ExpenseType type;
    private String description;
    private LocalDateTime created;
    private LocalDateTime updated;
}
