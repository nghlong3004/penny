package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.exception.ResourceException;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
public class TelegramProcessorExecutor {

    private static TelegramProcessorExecutor INSTANCE;
    private final TelegramClient client;

    public void executor(Long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
            log.debug("Sent message to chatId={}: {}", chatId, text);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}, message = {}", chatId, e.getMessage());
            throw new ResourceException(e.getMessage());
        }
    }

    public static TelegramProcessorExecutor getInstance(String token) {
        if (INSTANCE == null) {
            INSTANCE = new TelegramProcessorExecutor(token);
        }
        return INSTANCE;
    }

    private TelegramProcessorExecutor(String token) {
        this.client = new OkHttpTelegramClient(token);
    }

}
