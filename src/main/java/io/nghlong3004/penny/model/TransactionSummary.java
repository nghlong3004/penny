package io.nghlong3004.penny.model;

import io.nghlong3004.penny.model.type.TransactionType;

public record TransactionSummary(
        TransactionType type,
        Double totalAmount
) {
}
