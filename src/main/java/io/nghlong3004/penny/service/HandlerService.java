package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.Consumer;
import io.nghlong3004.penny.model.Penner;
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

    private static final Map<Long, Consumer> statuses = new ConcurrentHashMap<>();
    private static final TelegramExecutor EXECUTOR = ObjectContainer.getTelegramExecutor();
    private static final PennerService PENNER_SERVICE = ObjectContainer.getPennerService();
    private static final int RETRY_TIME = 4;

    public abstract void handle(Update update);

    public static void loadStatuses() {
        PENNER_SERVICE.getAllPenner()
                      .forEach(penner -> statuses.put(penner.getChatId(), Consumer.builder()
                                                                                  .status(penner.getStatus())
                                                                                  .firstName(penner.getFirstName())
                                                                                  .lastName(penner.getLastName())
                                                                                  .build()));
    }

    protected void execute(Long chatId, String message) {
        EXECUTOR.sendMessage(chatId, message, RETRY_TIME);
    }

    protected void execute(Long chatId, Sticker sticker) {
        EXECUTOR.sendSticker(chatId, sticker, RETRY_TIME);
    }

    protected void execute(Long chatId, Animation animation) {
        EXECUTOR.sendAnimation(chatId, animation, RETRY_TIME);
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
        Consumer consumer = statuses.get(chatId);
        consumer.setStatus(status);
        statuses.put(chatId, consumer);
        PENNER_SERVICE.updatePenner(chatId, status);
    }

    protected PennerType getStatus(Long chatId) {
        return statuses.get(chatId).getStatus();
    }

    protected String getFullName(Long chatId) {
        Consumer consumer = statuses.get(chatId);
        String firstName = consumer.getFirstName() == null ? "" : consumer.getFirstName();
        String lastName = consumer.getLastName() == null ? "" : consumer.getLastName();
        String blank = lastName.isBlank() ? "" : " ";
        return firstName + blank + lastName;
    }

    protected void addPenner(Penner penner) {
        PENNER_SERVICE.addPenner(penner);
    }

    protected void updatePenner(Penner penner) {
        PENNER_SERVICE.updatePenner(penner);
    }

}
