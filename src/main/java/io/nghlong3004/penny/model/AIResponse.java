package io.nghlong3004.penny.model;

public record AIResponse(
        String description,
        Double amount,
        String type,
        String date,
        String error,
        String reply
) {
}
