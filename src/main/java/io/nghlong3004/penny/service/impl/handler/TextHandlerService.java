package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.service.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class TextHandlerService extends HandlerService {

    private static volatile HandlerService instance;

    public static HandlerService getInstance() {
        if (instance == null) {
            synchronized (TextHandlerService.class) {
                if (instance == null) {
                    instance = new TextHandlerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {

    }

    private TextHandlerService() {
    }
}
