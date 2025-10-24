package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.Consumer;
import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.telegram.TelegramExecutor;
import io.nghlong3004.penny.util.InlineKeyboardUtil;
import io.nghlong3004.penny.util.ObjectContainer;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HandlerService {

    private static final Map<Long, Consumer> statuses = new ConcurrentHashMap<>();
    private static final TelegramExecutor EXECUTOR = ObjectContainer.getTelegramExecutor();
    private static final PennerService PENNER_SERVICE = ObjectContainer.getPennerService();
    protected static final int RETRY_TIME = 4;

    public abstract void handle(Update update);

    public static void loadStatuses() {
        PENNER_SERVICE.getAllPenner()
                      .forEach(penner -> statuses.put(penner.getChatId(), Consumer.builder()
                                                                                  .status(penner.getStatus())
                                                                                  .firstName(penner.getFirstName())
                                                                                  .lastName(penner.getLastName())
                                                                                  .spreadsheetsId(
                                                                                          penner.getSpreadsheetsId())
                                                                                  .build()));
    }

    protected void execute(Long chatId, String message) {
        EXECUTOR.sendMessage(chatId, message, RETRY_TIME);
    }

    protected void execute(Long chatId, ActionType actionType) {
        EXECUTOR.sendChatAction(chatId, actionType, RETRY_TIME);
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

    protected void update(Long chatId, PennerType status, String spreadsheetsId) {
        Consumer consumer = statuses.get(chatId);
        if (status != null) {
            consumer.setStatus(status);
        }
        if (spreadsheetsId != null) {
            consumer.setSpreadsheetsId(spreadsheetsId);
        }
        update(consumer, chatId);
    }

    protected PennerType getStatus(Long chatId) {
        Consumer consumer = statuses.get(chatId);
        if (consumer == null) {
            return null;
        }
        return consumer.getStatus();
    }

    protected String getSpreadsheetsId(Long chatId) {
        Consumer consumer = statuses.get(chatId);
        if (consumer == null) {
            return null;
        }
        return consumer.getSpreadsheetsId();
    }

    protected String getFullName(Long chatId) {
        Consumer consumer = statuses.get(chatId);
        String firstName = consumer.getFirstName() == null ? "" : consumer.getFirstName();
        String lastName = consumer.getLastName() == null ? "" : consumer.getLastName();
        String blank = lastName.isBlank() ? "" : " ";
        return firstName + blank + lastName;
    }

    protected void addPenner(Penner penner) {
        Consumer consumer = Consumer.builder()
                                    .firstName(penner.getFirstName())
                                    .lastName(penner.getLastName())
                                    .spreadsheetsId(penner.getSpreadsheetsId())
                                    .status(penner.getStatus())
                                    .build();
        statuses.put(penner.getChatId(), consumer);
        PENNER_SERVICE.addPenner(penner);
    }

    protected void update(Consumer consumer, Long chatId) {
        statuses.put(chatId, consumer);
        Penner penner = Penner.builder()
                              .chatId(chatId)
                              .firstName(consumer.getFirstName())
                              .lastName(consumer.getLastName())
                              .spreadsheetsId(consumer.getSpreadsheetsId())
                              .status(consumer.getStatus())
                              .build();
        PENNER_SERVICE.updatePenner(penner);
    }

}
