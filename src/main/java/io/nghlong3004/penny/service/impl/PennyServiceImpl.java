package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.service.PennyService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class PennyServiceImpl implements PennyService {

    private final TelegramBotsLongPollingApplication botsLongPollingApplication;

    private static PennyService instance;

    @Override
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

    public static PennyService getInstance() {
        if (instance == null) {
            instance = new PennyServiceImpl();
        }
        return instance;
    }

    private PennyServiceImpl() {
        this.botsLongPollingApplication = new TelegramBotsLongPollingApplication();
    }

    private void holdThread() throws InterruptedException {
        Thread.currentThread()
              .join();
    }

    private String getToken() {
        return ObjectContainer.getApplication()
                              .getTelegramToken();
    }

    private LongPollingSingleThreadUpdateConsumer getUpdateConsumer() {
        return PennyUpdateConsumerServiceImpl.builder()
                                             .pennerService(ObjectContainer.getPennerService())
                                             .pennyBotService(ObjectContainer.getPennyBotService())
                                             .build();
    }

}
