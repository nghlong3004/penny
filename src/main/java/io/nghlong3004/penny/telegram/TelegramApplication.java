package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.ObjectContainer;
import io.nghlong3004.penny.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class TelegramApplication {

    private static TelegramApplication INSTANCE;
    private final String token;
    private final ExecutorService updateProcessorPool;

    public void run() {
        try (TelegramBotsLongPollingApplication botsLongPollingApplication = new TelegramBotsLongPollingApplication()) {
            shutdown();
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

    private void shutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownConsumer();
            ThreadPoolUtil.shutdown();
        }));
    }

    private void shutdownConsumer() {
        log.info("Shutting down update processor pool...");
        updateProcessorPool.shutdown();
        try {
            if (!updateProcessorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Pool did not terminate in 5 seconds, forcing shutdown...");
                updateProcessorPool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            log.error("Shutdown was interrupted", ie);
            Thread.currentThread().interrupt();
            updateProcessorPool.shutdownNow();
        }
        log.info("Update processor pool shutdown complete.");
    }

    public static TelegramApplication getInstance(String token) {
        if (INSTANCE == null) {
            INSTANCE = new TelegramApplication(token);
        }
        return INSTANCE;
    }

    private TelegramApplication(String token) {
        this.token = token;
        this.updateProcessorPool = Executors.newFixedThreadPool(10);
    }

    private void holdThread() throws InterruptedException {
        Thread.currentThread().join();
    }

    private LongPollingSingleThreadUpdateConsumer getUpdateConsumer() {
        return TelegramConsumer.builder()
                               .messageHandler(ObjectContainer.getMessageHandlerService())
                               .updateProcessorPool(this.updateProcessorPool)
                               .build();
    }

}
