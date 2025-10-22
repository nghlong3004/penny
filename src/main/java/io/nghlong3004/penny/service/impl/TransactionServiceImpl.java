package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutor;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.type.ColumnType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.model.type.TransactionType;
import io.nghlong3004.penny.repository.TransactionRepository;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.TransactionService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.telegram.telegrambots.meta.api.objects.message.Message;

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


    private final PennerService pennerService;


    @Override
    public void addIncome(Message message, AIResponse aiResponse, Penner penner) {
        int month = getMonth(aiResponse.date());
        int indexRowWrite = getIndexRowWrite(month, penner.getSpreadsheetsId(), ColumnType.INCOME);
        write(month, penner.getSpreadsheetsId(), ColumnType.INCOME, indexRowWrite, aiResponse);
        Transaction transaction = ofTransaction(penner.getChatId(), aiResponse);
        addTransaction(transaction);

    }

    @Override
    public void addExpense(Message message, AIResponse aiResponse, Penner penner) {
        int month = getMonth(aiResponse.date());
        int indexRowWrite = getIndexRowWrite(month, penner.getSpreadsheetsId(), ColumnType.EXPENSE);
        write(month, penner.getSpreadsheetsId(), ColumnType.EXPENSE, indexRowWrite, aiResponse);
        Transaction transaction = ofTransaction(penner.getChatId(), aiResponse);
        addTransaction(transaction);

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
    public boolean checkinPenner(Penner penner, String message) {
        return switch (penner.getStatus()) {
            case NOT_LINKED -> handlePennerNotLink(penner.getChatId());
            case PENDING -> handlePennerPending(penner, message);
            case LINKED -> true;
        };
    }

    private TransactionServiceImpl() {
        googleSheetsProcessorExecutor = ObjectContainer.getGoogleSheetsProcessorExecutor();
        pennerService = ObjectContainer.getPennerService();
    }

    private boolean handlePennerNotLink(Long chatId) {

        return false;
    }

    private boolean handlePennerPending(Penner penner, String message) {
        Optional<String> extractedId = extractSpreadsheetId(message);
        if (extractedId.isPresent()) {
            penner.setStatus(PennerType.LINKED);
            penner.setSpreadsheetsId(extractedId.get());
            pennerService.updatePenner(penner);

            log.info("Spreadsheet linked successfully for chatId={}", penner.getChatId());
        }
        else {

            log.warn("Invalid spreadsheet link provided by chatId={}", penner.getChatId());
        }
        return false;
    }

    private void addTransaction(Transaction transaction) {
        try (SqlSession session = ObjectContainer.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            transactionRepository.insert(transaction);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());

        }
    }

    private int getIndexRowWrite(int month, String spreadsheetsId, ColumnType column) {
        String readRange = String.format("Tháng %d!%c:%c", month, column.getValue(), column.getValue());
        String[] data = googleSheetsProcessorExecutor.readFromSheet(spreadsheetsId, readRange).split("\n");
        return data.length + 1;
    }

    private void write(int month, String spreadsheetsId, ColumnType column, int index, AIResponse aiResponse) {
        List<Object> value = null;
        if (column == ColumnType.EXPENSE) {
            value = Arrays.asList(aiResponse.date(), aiResponse.description(), aiResponse.amount(), aiResponse.type());
        }
        else {
            value = Arrays.asList(aiResponse.date(), aiResponse.description(), aiResponse.amount());
        }
        List<List<Object>> data = List.of(value);
        String writeRange = String.format("Tháng %d!%c%d", month, column.getValue(), index);
        googleSheetsProcessorExecutor.writeToSheet(spreadsheetsId, writeRange, data);
    }

    private Transaction ofTransaction(Long chatId, AIResponse aiResponse) {
        TransactionType type = TransactionType.valueOf(aiResponse.type().toUpperCase());
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
