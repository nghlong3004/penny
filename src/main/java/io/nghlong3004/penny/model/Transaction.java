package io.nghlong3004.penny.model;

import io.nghlong3004.penny.model.type.TransactionType;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record Transaction(
        Long id,
        Long chatId,
        Double amount,
        TransactionType type,
        String description,
        Timestamp dated,
        Timestamp created
) {

}
