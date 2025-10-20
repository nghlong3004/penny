package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Expense;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpenseRepository {
    @Insert("""
            INSERT INTO expense (chat_id, amount, description, type)
            VALUES (#{chatId}, #{amount}, #{description}, #{type}::expense_type);
            """)
    void insert(Expense expense);
}
