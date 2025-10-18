package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.service.PennyBotService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
public class PennyBotServiceImpl implements PennyBotService {

    private final TelegramClient client;

    private static PennyBotService instance;

    @Override
    public void send(Long chatId, Object message) {
        try {
            client.execute(SendMessage.builder()
                                      .chatId(chatId)
                                      .text((String) message)
                                      .build());
            log.debug("Sent message to chatId={}: {}", chatId, message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}, message = {}", chatId, e.getMessage());
            throw new ResourceException(e.getMessage());
        }
    }

    public static PennyBotService getInstance(String token) {
        if (instance == null) {
            instance = new PennyBotServiceImpl(token);
        }
        return instance;
    }

    private PennyBotServiceImpl(String token) {
        this.client = new OkHttpTelegramClient(token);
    }
}
