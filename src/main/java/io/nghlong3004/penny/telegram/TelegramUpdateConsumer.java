package io.nghlong3004.penny.telegram;

import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.service.PennerService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Builder
public class TelegramUpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final PennerService pennerService;
    private final TelegramProcessorExecutor telegramProcessorExecutor;

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.debug("This is not text message");
            throw new ResourceException("This is not text message");
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        String text = message.getText();
        log.info("chatId = {} send message with text = {} ", chat.getId(), text);
        updatePenner(chat);

        telegramProcessorExecutor.executor(chat.getId(), "reply: " + text);
    }

    private void updatePenner(Chat chat) {
        String firstName = chat.getFirstName() == null ? "" : chat.getFirstName();
        String lastName = chat.getLastName() == null ? "" : " " + chat.getLastName();
        Penner penner = pennerService.getPenner(chat.getId(), firstName, lastName);
        if (penner.getId() == null) {
            pennerService.addPenner(penner);
        }
        else {
            pennerService.updatePenner(penner);
        }
    }
}
