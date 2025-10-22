package io.nghlong3004.penny.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ColumnType {
    INCOME('A'),
    EXPENSE('D');
    private final char value;
}
