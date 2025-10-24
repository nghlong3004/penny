package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionRepository {
    @Insert("""
            INSERT INTO transaction (chat_id, amount, description, type)
            VALUES (#{chatId}, #{amount}, #{description}, #{type}::transaction_type);
            """)
    void insert(Transaction transaction);

    @Select("""
            SELECT * FROM transaction
            WHERE chat_id = #{chatId}
            """)
    List<Transaction> getAllByChatId(
            @Param("chatId") Long chatId);
}
