package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.type.ColumnType;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface TransactionService {
    boolean add(Long chatId, AIResponse aiResponse);

    boolean write(String spreadsheetsId, ColumnType column, AIResponse aiResponse) throws IOException;

    public boolean isSheetWriteable(String spreadsheetsId) throws IOException;

    void getTotalIncome(Long chatId);

    void getTotalIncomeFromInTo(Long chatId, Timestamp from, Timestamp to);

    void getTotalExpense(Long chatId);

    void getTotalExpenseFromInTo(Long chatId, Timestamp from, Timestamp to);

    List<Transaction> getAllTransactionByChatId(Long chatId);
}
