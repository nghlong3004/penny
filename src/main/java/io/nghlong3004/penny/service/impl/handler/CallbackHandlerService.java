package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.model.type.CallbackType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallbackHandlerService extends HandlerService {

    private static volatile HandlerService instance;

    public static HandlerService getInstance() {
        if (instance == null) {
            synchronized (CallbackHandlerService.class) {
                if (instance == null) {
                    instance = new CallbackHandlerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        if (CallbackType.YES_LINK.getReply().equals(data)) {
            updateStatus(chatId, PennerType.PENDING);
            execute(chatId, CallbackType.YES_LINK.getReply());
            execute(chatId, FileLoaderUtil.loadFile(CommandType.LINK.getFilePath()));
            execute(chatId, FileLoaderUtil.loadFile(CommandType.SHEETS.getFilePath()));
        }
        else if (CallbackType.NO_LINK.getReply().equals(data)) {
            execute(chatId, CallbackType.NO_LINK.getReply());
        }

    }
}
