package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.TransactionSummary;
import io.nghlong3004.penny.model.type.ColumnType;
import io.nghlong3004.penny.model.type.CommandType;
import org.apache.ibatis.exceptions.PersistenceException;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    void add(Long chatId, AIResponse aiResponse) throws PersistenceException;

    void write(String spreadsheetsId, ColumnType column, AIResponse aiResponse) throws IOException;

    boolean isSheetWriteable(String spreadsheetsId) throws IOException;

    List<Transaction> getAllTransactionByChatId(Long chatId) throws PersistenceException;

    List<TransactionSummary> getTransactionSummary(Long chatId, CommandType type) throws PersistenceException;

    List<Transaction> findTransactions(Long chatId, String searchTerm) throws PersistenceException;

    Transaction undoLastTransaction(Long chatId) throws PersistenceException;

    List<Transaction> getRecentTransactions(Long chatId) throws PersistenceException;

    void deleteTransaction(Long id, Long chatId) throws PersistenceException;

}
