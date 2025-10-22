package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

@Slf4j
public final class TelegramApplication {

    private static TelegramApplication INSTANCE;
    private final String token;

    public void run() {
        try (TelegramBotsLongPollingApplication botsLongPollingApplication = new TelegramBotsLongPollingApplication()) {
            HandlerService.loadStatuses();
            LongPollingSingleThreadUpdateConsumer updateConsumerService = getUpdateConsumer();
            log.debug("Register Bot Telegram...");
            botsLongPollingApplication.registerBot(token, updateConsumerService);
            log.debug("Register Successfully");
            holdThread();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TelegramApplication getInstance(String token) {
        if (INSTANCE == null) {
            INSTANCE = new TelegramApplication(token);
        }
        return INSTANCE;
    }

    private TelegramApplication(String token) {
        this.token = token;
    }

    private void holdThread() throws InterruptedException {
        Thread.currentThread().join();
    }

    private LongPollingSingleThreadUpdateConsumer getUpdateConsumer() {
        return TelegramConsumer.builder().messageHandler(ObjectContainer.getMessageHandlerService()).build();
    }

}
