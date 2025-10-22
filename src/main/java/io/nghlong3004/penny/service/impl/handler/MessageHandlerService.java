package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class MessageHandlerService extends HandlerService {

    private static volatile HandlerService instance;
    private final HandlerService textHandler;
    private final HandlerService commandHandler;
    private final HandlerService callbackHandler;
    private final PennerService pennerService;

    public static HandlerService getInstance(HandlerService textHandler, HandlerService commandHandler,
                                             HandlerService callbackHandler, PennerService pennerService) {
        if (instance == null) {
            synchronized (MessageHandlerService.class) {
                if (instance == null) {
                    instance = new MessageHandlerService(textHandler, commandHandler, callbackHandler, pennerService);
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {

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
        if (message.isCommand()) {
            commandHandler.handle(update);
        }
        else {
            textHandler.handle(update);
        }
        if (getStatus(chatId) == PennerType.NOT_LINKED) {
            execute(chatId, FileLoaderUtil.loadFile("command/tips.txt"));
        }
    }

    private void updateConsumes(Message message) {
        Long chatId = message.getChatId();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        if (getStatus(chatId) == null) {
            updateStatus(chatId, PennerType.NOT_LINKED);
            pennerService.addPenner(ofPenner(chatId, firstName, lastName));
        }
    }

    private MessageHandlerService(HandlerService textHandler, HandlerService commandHandler,
                                  HandlerService callbackHandler, PennerService pennerService) {
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;
        this.pennerService = pennerService;

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
