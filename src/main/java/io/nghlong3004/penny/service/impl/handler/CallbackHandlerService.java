package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.GifConstant;
import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.type.CallbackType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import io.nghlong3004.penny.util.GifLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class CallbackHandlerService extends HandlerService {

    private static volatile HandlerService instance;

    public static HandlerService getInstance() {
        if (instance == null) {
            synchronized (CallbackHandlerService.class) {
                if (instance == null) {
                    log.info("Creating new CallbackHandlerService instance...");
                    instance = new CallbackHandlerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        PennerType status = getStatus(chatId);
        execute(chatId, ActionType.TYPING);
        log.debug("Handling callback query. ChatID: {}, Data: {}, Status: {}", chatId, data, status);
        if (CallbackType.YES_LINK.getReply().equals(data)) {

            if (status == PennerType.NOT_LINKED) {
                handleYesLink(chatId);
            }
            else {
                handleAlreadyClicked(chatId, data, status);
            }

        }
        else if (CallbackType.NO_LINK.getReply().equals(data)) {

            if (status == PennerType.NOT_LINKED) {
                handleNoLink(chatId);
            }
            else {
                handleAlreadyClicked(chatId, data, status);
            }

        }
        else {
            log.warn("Unhandled callback data received. ChatID: {}, Data: {}", chatId, data);
        }
    }

    private void handleYesLink(long chatId) {
        log.info("ChatID: {}. User (NOT_LINKED) clicked YES. Updating status to PENDING.", chatId);

        update(chatId, PennerType.PENDING, null);
        execute(chatId, CallbackType.YES_LINK.getReply());
        execute(chatId, FileLoaderUtil.loadFile(CommandType.LINK.getFilePath()));
        execute(chatId, FileLoaderUtil.loadFile(CommandType.SHEETS.getFilePath()));
    }

    private void handleNoLink(long chatId) {
        log.info("ChatID: {}. User (NOT_LINKED) clicked NO.", chatId);

        execute(chatId, CallbackType.NO_LINK.getReply());
        execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.SAD)).caption(null).build());
    }

    private void handleAlreadyClicked(long chatId, String data, PennerType currentStatus) {
        log.warn("ChatID: {}. Callback was ignored. Data: {}. Reason: Status was {}, not NOT_LINKED.", chatId, data,
                 currentStatus);

        execute(chatId, "Bạn đã click vào rồi");
        execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.STOP)).caption(null).build());
    }

}
