package io.nghlong3004.penny.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Expense {
    private Long id;
    private Long pennerId;
    private Double amount;
    private ExpenseType type;
    private String description;
    private Timestamp created;
    private Timestamp updated;
}
