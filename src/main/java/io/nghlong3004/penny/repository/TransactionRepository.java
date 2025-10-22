package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionRepository {
    @Insert("""
            INSERT INTO transaction (chat_id, amount, description, type)
            VALUES (#{chatId}, #{amount}, #{description}, #{type}::transaction_type);
            """)
    void insert(Transaction transaction);
}
