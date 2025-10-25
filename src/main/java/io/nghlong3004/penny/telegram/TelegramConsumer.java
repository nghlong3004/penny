package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.handler.HandlerService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class TelegramConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final HandlerService messageHandler;
    private final ExecutorService updateProcessorPool;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            updateProcessorPool.submit(() -> {
                try {
                    messageHandler.handle(update);
                } catch (Exception e) {
                    log.error("Unhandled exception during async update processing. Error: {}", e.getMessage(), e);
                }
            });
        }
    }
}
