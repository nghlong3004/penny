package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutor;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.TransactionSummary;
import io.nghlong3004.penny.model.type.ColumnType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.TransactionType;
import io.nghlong3004.penny.repository.TransactionRepository;
import io.nghlong3004.penny.service.TransactionService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Getter
    private static final TransactionService instance = new TransactionServiceImpl();

    private final GoogleSheetsProcessorExecutor googleSheetsProcessorExecutor;


    @Override
    public void add(Long chatId, AIResponse aiResponse) {
        Transaction transaction = ofTransaction(chatId, aiResponse);
        addTransaction(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionByChatId(Long chatId) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            List<Transaction> transactions = transactionRepository.getAllByChatId(chatId);
            if (transactions == null) {
                return List.of();
            }
            session.commit();
            return transactions;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public List<TransactionSummary> getTransactionSummary(Long chatId, CommandType type) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            List<TransactionSummary> transactionSummaries = switch (type) {
                case DAILY -> transactionRepository.getDailySummary(chatId);
                case MONTHLY -> transactionRepository.getMonthlySummary(chatId);
                case WEEKLY -> transactionRepository.getWeeklySummary(chatId);
                default -> null;
            };
            if (transactionSummaries == null) {
                return List.of();
            }
            session.commit();
            return transactionSummaries;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public List<Transaction> findTransactions(Long chatId, String searchTerm) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            List<Transaction> transactions = transactionRepository.findTransactions(chatId, searchTerm);
            if (transactions == null) {
                return List.of();
            }
            session.commit();
            return transactions;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public Transaction undoLastTransaction(Long chatId) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            Transaction transactions = transactionRepository.getLastTransaction(chatId);
            if (transactions != null) {
                transactionRepository.deleteById(chatId, transactions.id());
            }
            session.commit();
            return transactions;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public List<Transaction> getRecentTransactions(Long chatId) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            List<Transaction> transactions = transactionRepository.getRecentTransactions(chatId);
            if (transactions == null) {
                return List.of();
            }
            session.commit();
            return transactions;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public void deleteTransaction(Long id, Long chatId) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            transactionRepository.deleteTransactionById(id, chatId);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public boolean isSheetWriteable(String spreadsheetsId) throws IOException {
        String writeRange = String.format("Tháng %d!%c%d", 10, 'H', 1);
        googleSheetsProcessorExecutor.writeToSheet(spreadsheetsId, writeRange, List.of(List.of()));
        return true;
    }

    @Override
    public void write(String spreadsheetsId, ColumnType column, AIResponse aiResponse) throws IOException {
        int month = getMonth(aiResponse.date());
        int index = getIndexRowWrite(month, spreadsheetsId, column);
        List<Object> value = null;
        if (column == ColumnType.EXPENSE) {
            value = Arrays.asList(aiResponse.date(), aiResponse.description(), aiResponse.amount(),
                                  aiResponse.type().toString());
        }
        else {
            value = Arrays.asList(aiResponse.date(), aiResponse.description(), aiResponse.amount());
        }
        List<List<Object>> data = List.of(value);
        String writeRange = String.format("Tháng %d!%c%d", month, column.getValue(), index);
        googleSheetsProcessorExecutor.writeToSheet(spreadsheetsId, writeRange, data);
    }

    private Transaction ofTransaction(Long chatId, AIResponse aiResponse) {
        TransactionType type = aiResponse.type();
        LocalDate date = LocalDate.parse(aiResponse.date(), DateTimeFormatter.ISO_LOCAL_DATE);
        return Transaction.builder()
                          .description(aiResponse.description())
                          .amount(aiResponse.amount())
                          .chatId(chatId)
                          .type(type)
                          .dated(Timestamp.valueOf(date.atStartOfDay()))
                          .build();
    }


    private TransactionServiceImpl() {
        googleSheetsProcessorExecutor = ObjectContainer.getGoogleSheetsProcessorExecutor();
    }

    private void addTransaction(Transaction transaction) throws PersistenceException {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            transactionRepository.insert(transaction);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    private int getIndexRowWrite(int month, String spreadsheetsId, ColumnType column) throws IOException {
        String readRange = String.format("Tháng %d!%c:%c", month, column.getValue(), column.getValue());
        String[] data = googleSheetsProcessorExecutor.readFromSheet(spreadsheetsId, readRange).split("\n");
        return data.length + 1;
    }


    private int getMonth(String date) {
        return Integer.parseInt(date.substring(5, 7));
    }
}
