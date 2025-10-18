package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.PennyBotService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Builder
public class PennyUpdateConsumerServiceImpl implements LongPollingSingleThreadUpdateConsumer {

    private final PennerService pennerService;
    private final PennyBotService pennyBotService;

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage()
                                           .hasText()) {
            log.debug("This is not text message");
            throw new ResourceException("This is not text message");
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        String text = message.getText();
        log.info("{} {} with chatId = {} send message with text = {} ", chat.getFirstName(), chat.getLastName(),
                 chat.getId(), text);
        pennyBotService.send(chat.getId(), "reply: " + text);
    }
}
