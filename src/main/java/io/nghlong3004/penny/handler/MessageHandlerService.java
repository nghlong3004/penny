package io.nghlong3004.penny.handler;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.util.FileLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
public class MessageHandlerService extends HandlerService {

    private static volatile HandlerService instance;
    private final HandlerService textHandler;
    private final HandlerService commandHandler;
    private final HandlerService callbackHandler;

    public static HandlerService getInstance(HandlerService textHandler, HandlerService commandHandler,
                                             HandlerService callbackHandler) {
        if (instance == null) {
            synchronized (MessageHandlerService.class) {
                if (instance == null) {
                    log.info("Creating new MessageHandlerService instance...");
                    instance = new MessageHandlerService(textHandler, commandHandler, callbackHandler);
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {
        log.debug("Received update. ID: {}", update.hashCode());
        if (update.hasMessage()) {
            handleMessage(update);
        }

        if (update.hasCallbackQuery()) {
            callbackHandler.handle(update);
        }

    }

    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        Message message = update.getMessage();
        updateConsumes(message);
        execute(chatId, ActionType.TYPING);
        log.debug("Handling message for ChatID: {}. IsCommand: {}, HasText: {}", chatId, message.isCommand(),
                  message.hasText());
        if (message.isCommand()) {
            commandHandler.handle(update);
        }
        else if (message.hasText()) {
            textHandler.handle(update);
        }
        if (getStatus(chatId) == PennerType.NOT_LINKED && getTips(chatId)) {
            log.debug("ChatID: {}. Status is NOT_LINKED, sending tips.", chatId);
            execute(chatId, FileLoaderUtil.loadFile("command/tips.html"));
        }
    }

    private void updateConsumes(Message message) {
        Long chatId = message.getChatId();
        if (getStatus(chatId) == null) {
            String firstName = message.getChat().getFirstName();
            String lastName = message.getChat().getLastName();

            log.info("New user detected. Creating Penner for ChatID: {}. Name: {} {}", chatId, firstName, lastName);

            addPenner(ofPenner(chatId, firstName, lastName));
        }
    }

    private MessageHandlerService(HandlerService textHandler, HandlerService commandHandler,
                                  HandlerService callbackHandler) {
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;

    }

    private Penner ofPenner(Long chatId, String firstName, String lastName) {
        return Penner.builder()
                     .chatId(chatId)
                     .lastName(lastName)
                     .firstName(firstName)
                     .status(PennerType.NOT_LINKED)
                     .build();
    }
}
