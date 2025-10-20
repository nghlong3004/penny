package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TelegramApplication {

    private final TelegramBotsLongPollingApplication botsLongPollingApplication;

    private static TelegramApplication instance;

    public void run() {
        try {
            LongPollingSingleThreadUpdateConsumer updateConsumerService = getUpdateConsumer();
            log.debug("Register Bot Telegram...");
            botsLongPollingApplication.registerBot(getToken(), updateConsumerService);
            log.debug("Register Successfully");
            holdThread();
        } catch (TelegramApiException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static TelegramApplication getInstance() {
        if (instance == null) {
            instance = new TelegramApplication();
        }
        return instance;
    }

    private TelegramApplication() {
        this.botsLongPollingApplication = new TelegramBotsLongPollingApplication();
    }

    private void holdThread() throws InterruptedException {
        Thread.currentThread().join();
    }

    private String getToken() {
        return ObjectContainer.getApplication().getTelegramToken();
    }

    private LongPollingSingleThreadUpdateConsumer getUpdateConsumer() {
        return TelegramUpdateConsumer.builder()
                                     .pennerService(ObjectContainer.getPennerService())
                                     .telegramProcessorExecutor(
                                             ObjectContainer.getTelegramProcessorExecutorProcessorExecutor())
                                     .build();
    }

}
