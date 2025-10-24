package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutor;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.type.ColumnType;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Getter
    private static final TransactionService instance = new TransactionServiceImpl();

    private static final Pattern GOOGLE_SHEET_LINK_PATTERN = Pattern.compile(
            "https://docs\\.google\\.com/spreadsheets/d/([a-zA-Z0-9-_]+)");

    private final GoogleSheetsProcessorExecutor googleSheetsProcessorExecutor;


    @Override
    public boolean add(Long chatId, AIResponse aiResponse) {
        Transaction transaction = ofTransaction(chatId, aiResponse);
        return addTransaction(transaction);
    }

    @Override
    public void getTotalIncome(Long chatId) {

    }

    @Override
    public void getTotalIncomeFromInTo(Long chatId, Timestamp from, Timestamp to) {

    }

    @Override
    public void getTotalExpense(Long chatId) {

    }

    @Override
    public void getTotalExpenseFromInTo(Long chatId, Timestamp from, Timestamp to) {

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

    private TransactionServiceImpl() {
        googleSheetsProcessorExecutor = ObjectContainer.getGoogleSheetsProcessorExecutor();
    }

    private boolean addTransaction(Transaction transaction) {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            transactionRepository.insert(transaction);
            session.commit();
            return true;
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            return false;
        }
    }

    private int getIndexRowWrite(int month, String spreadsheetsId, ColumnType column) throws IOException {
        String readRange = String.format("Tháng %d!%c:%c", month, column.getValue(), column.getValue());
        String[] data = googleSheetsProcessorExecutor.readFromSheet(spreadsheetsId, readRange).split("\n");
        return data.length + 1;
    }

    @Override
    public boolean isSheetWriteable(String spreadsheetsId) throws IOException {
        String writeRange = String.format("Tháng %d!%c%d", 10, 'H', 1);
        return googleSheetsProcessorExecutor.writeToSheet(spreadsheetsId, writeRange, List.of(List.of()));
    }

    @Override
    public boolean write(String spreadsheetsId, ColumnType column, AIResponse aiResponse) throws IOException {
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
        return googleSheetsProcessorExecutor.writeToSheet(spreadsheetsId, writeRange, data);
    }

    private Transaction ofTransaction(Long chatId, AIResponse aiResponse) {
        TransactionType type = aiResponse.type();
        LocalDate date = LocalDate.parse(aiResponse.date(), DateTimeFormatter.ISO_LOCAL_DATE);
        Timestamp created = Timestamp.valueOf(date.atStartOfDay());
        return Transaction.builder()
                          .description(aiResponse.description())
                          .amount(aiResponse.amount())
                          .chatId(chatId)
                          .type(type)
                          .created(created)
                          .build();
    }

    public static Optional<String> extractSpreadsheetId(String messageText) {
        Matcher matcher = GOOGLE_SHEET_LINK_PATTERN.matcher(messageText);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    private int getMonth(String date) {
        return Integer.parseInt(date.substring(5, 7));
    }
}
