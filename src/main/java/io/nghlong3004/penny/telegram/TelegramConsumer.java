package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.service.HandlerService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Builder
public class TelegramConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final HandlerService messageHandler;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            messageHandler.handle(update);
        }
    }
}
