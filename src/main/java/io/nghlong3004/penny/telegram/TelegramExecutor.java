package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.model.Sticker;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.InputStream;

@Slf4j
public final class TelegramExecutor {

    private static TelegramExecutor instance;
    private final TelegramClient telegramClient;

    public static TelegramExecutor getInstance(TelegramClient telegramClient) {
        if (instance == null) {
            instance = new TelegramExecutor(telegramClient);
        }
        return instance;
    }


    private TelegramExecutor(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void sendMessage(Long chatId, String message, int retryTime) {
        try {
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(message).build());
            log.debug("Sent message to chatId={}", chatId);
        } catch (TelegramApiException e) {
            if (retryTime > 0) {
                log.debug("Retry send message time: {}", retryTime);
                sendMessage(chatId, message, --retryTime);
            }
            else {
                log.error("Failed to send message to chatId={}, message = {}", chatId, e.getLocalizedMessage());
            }
        }
    }

    public void sendMessage(Long chatId, String message, InlineKeyboardMarkup keyboardMarkup, int retryTime) {
        try {
            telegramClient.execute(
                    SendMessage.builder().chatId(chatId).text(message).replyMarkup(keyboardMarkup).build());
            log.debug("Sent message with markup to chatId={}", chatId);
        } catch (TelegramApiException e) {
            if (retryTime > 0) {
                log.debug("Retry send message with markup time: {}", retryTime);
                sendMessage(chatId, message, --retryTime);
            }
            else {
                log.error("Failed to send message with markup to chatId={}, message = {}", chatId,
                          e.getLocalizedMessage());
            }
        }
    }

    public void sendSticker(Long chatId, Sticker sticker, int retryTime) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sticker.resourcePath())) {
            if (inputStream == null) {
                log.error("Sticker not found in resources: {}", sticker.resourcePath());
                return;
            }
            String fileName = new File(sticker.resourcePath()).getName();
            InputFile photo = new InputFile(inputStream, fileName);
            SendSticker sendSticker = SendSticker.builder().chatId(chatId).sticker(photo).build();
            telegramClient.execute(sendSticker);
            log.debug("Sent sticker from resources to chatId={}: {}", chatId, sticker.resourcePath());
        } catch (TelegramApiException e) {
            if (retryTime > 0) {
                log.debug("Retry send sticker time: {}", retryTime);
                sendSticker(chatId, sticker, --retryTime);
            }
            else {
                log.error("Failed to send sticker to chatId={}, error: {}", chatId, e.getLocalizedMessage());
            }
        } catch (Exception e) {
            log.error("Error read file from resources: {}", sticker.resourcePath(), e);
        }
    }

    public void sendPhoto(Long chatId, String resourcePath, String caption, int retryTime) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                log.error("File not found in resources: {}", resourcePath);
                return;
            }
            String fileName = new File(resourcePath).getName();
            InputFile photo = new InputFile(inputStream, fileName);
            SendPhoto sendPhoto = SendPhoto.builder().chatId(chatId).photo(photo).caption(caption).build();
            telegramClient.execute(sendPhoto);
            log.debug("Sent photo from resources to chatId={}: {}", chatId, resourcePath);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo to chatId={}, error: {}", chatId, e.getLocalizedMessage());
            if (retryTime > 0) {
                log.debug("Retry send photo: {}", retryTime);
                sendPhoto(chatId, resourcePath, caption, --retryTime);
            }
            else {
                log.error("Failed to send photo to chatId={}, error: {}", chatId, e.getLocalizedMessage());
            }
        } catch (Exception e) {
            log.error("Error read file from resources: {}", resourcePath, e);
        }
    }

}
