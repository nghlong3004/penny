package io.nghlong3004.penny.model;

import io.nghlong3004.penny.model.type.TransactionType;

public record AIResponse(
        String description,
        Double amount,
        TransactionType type,
        String date,
        String error,
        String reply
) {
}
