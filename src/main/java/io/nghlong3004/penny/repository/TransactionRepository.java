package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.TransactionSummary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionRepository {
    @Insert("""
            INSERT INTO transaction (chat_id, amount, description, type, dated)
            VALUES (#{chatId}, #{amount}, #{description}, #{type}::transaction_type, #{dated});
            """)
    void insert(Transaction transaction);

    @Select("""
            SELECT * FROM transaction
            WHERE chat_id = #{chatId}
            """)
    List<Transaction> getAllByChatId(
            @Param("chatId") Long chatId);

    @Select("""
            SELECT
                type,
                SUM(amount) AS total_amount
            FROM
                transaction
            WHERE
                chat_id = #{chatId}
                AND created >= CURRENT_DATE
                AND created < (CURRENT_DATE + INTERVAL '1 day')
            GROUP BY
                type;
            """)
    List<TransactionSummary> getDailySummary(
            @Param("chatId") Long chatId);

    @Select("""
            SELECT
                type,
                SUM(amount) AS total_amount
            FROM
                transaction
            WHERE
                chat_id = #{chatId}
                AND created >= (CURRENT_DATE - INTERVAL '6 days')
                AND created < (CURRENT_DATE + INTERVAL '1 day')
            GROUP BY
                type;
            """)
    List<TransactionSummary> getWeeklySummary(
            @Param("chatId") Long chatId);

    @Select("""
            SELECT
                type,
                SUM(amount) AS total_amount
            FROM
                transaction
            WHERE
                chat_id = #{chatId}
                AND date_trunc('month', created) = date_trunc('month', CURRENT_DATE)
            GROUP BY
                type;
            """)
    List<TransactionSummary> getMonthlySummary(
            @Param("chatId") Long chatId);

    @Select("""
            SELECT
                *
            FROM
                transaction
            WHERE
                chat_id = #{chatId}
                AND description ILIKE #{searchTerm}
            ORDER BY
                created DESC
            LIMIT 10;
            """)
    List<Transaction> findTransactions(
            @Param("chatId") Long chatId,
            @Param("searchTerm") String searchTerm);

    @Select("""
            SELECT * FROM transaction
            WHERE chat_id = #{chatId}
            ORDER BY created DESC
            LIMIT 1
            """)
    Transaction getLastTransaction(
            @Param("chatId") Long chatId);

    @Delete("""
            DELETE FROM transaction
            WHERE id = #{transactionId} AND chat_id = #{chatId}
            """)
    void deleteById(
            @Param("chatId") Long chatId,
            @Param("transactionId") Long transactionId);

    @Select("""
            SELECT
                *
            FROM
                transaction
            WHERE
                chat_id = #{chatId}
            ORDER BY
                created DESC
            LIMIT 5;
            """)
    List<Transaction> getRecentTransactions(
            @Param("chatId") Long chatId);

    @Delete("""
            DELETE FROM
                transaction
            WHERE
                id = #{id}
                AND chat_id = #{chatId};
            """)
    void deleteTransactionById(
            @Param("id") Long id,
            @Param("chatId") Long chatId);
}
