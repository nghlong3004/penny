package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.telegram.TelegramExecutor;
import io.nghlong3004.penny.util.InlineKeyboardUtil;
import io.nghlong3004.penny.util.ObjectContainer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HandlerService {

    private static final Map<Long, PennerType> statuses = new ConcurrentHashMap<>();
    private static final TelegramExecutor EXECUTOR = ObjectContainer.getTelegramExecutor();
    private static final PennerService PENNER_SERVICE = ObjectContainer.getPennerService();
    private static final int RETRY_TIME = 4;

    public abstract void handle(Update update);

    public static void loadStatuses() {
        PENNER_SERVICE.getAllPenner().forEach(penner -> statuses.put(penner.getChatId(), penner.getStatus()));
    }

    protected void execute(Long chatId, String message) {
        EXECUTOR.sendMessage(chatId, message, RETRY_TIME);
    }

    protected void execute(Long chatId, Sticker sticker) {
        EXECUTOR.sendSticker(chatId, sticker, RETRY_TIME);
    }

    protected void execute(Long chatId, List<String> messages) {
        messages.forEach(message -> execute(chatId, message));
    }

    protected void execute(Long chatId, String resourcePath, String caption) {
        EXECUTOR.sendPhoto(chatId, resourcePath, caption, RETRY_TIME);
    }

    protected void execute(Long chatId, String message, List<Map<String, String>> data) {
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardUtil.createKeyboard(data);
        EXECUTOR.sendMessage(chatId, message, keyboardMarkup, RETRY_TIME);
    }

    protected void updateStatus(Long chatId, PennerType status) {
        statuses.put(chatId, status);
    }

    protected PennerType getStatus(Long chatId) {
        return statuses.get(chatId);
    }

}
