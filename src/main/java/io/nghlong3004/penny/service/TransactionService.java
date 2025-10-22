package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Penner;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.sql.Timestamp;

public interface TransactionService {
    void addIncome(Message message, AIResponse aiResponse, Penner penner);

    void addExpense(Message message, AIResponse aiResponse, Penner penner);

    boolean checkinPenner(Penner penner, String message);

    void getTotalIncome(Long chatId);

    void getTotalIncomeFromInTo(Long chatId, Timestamp from, Timestamp to);

    void getTotalExpense(Long chatId);

    void getTotalExpenseFromInTo(Long chatId, Timestamp from, Timestamp to);
}
