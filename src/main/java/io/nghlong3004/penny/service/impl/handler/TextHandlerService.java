package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.TelegramConstant;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import io.nghlong3004.penny.util.GoogleSheetsUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

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
        Message message = update.getMessage();
        if (getStatus(message.getChatId()) == PennerType.PENDING) {
            Optional<String> extractedId = GoogleSheetsUtil.extractSpreadsheetId(message.getText());
            if (extractedId.isPresent()) {
                handleLinkingStatus(extractedId.get(), message);
            }
            else {
                String text = FileLoaderUtil.loadFile(CommandType.IN_PENDING.getFilePath());
                execute(message.getChatId(), text);
                execute(message.getChatId(), new Sticker(TelegramConstant.DEFAULT_JPG));
            }
        }
        else {
            handleTextMessage(message);
        }


    }

    private void handleTextMessage(Message message) {

    }

    private void handleLinkingStatus(String messageText, Message message) {
        updateStatus(message.getChatId(), PennerType.LINKED);
        message.setText("Liên kết Google Sheet thành công!");
        log.info("Spreadsheet linked successfully for chatId={}", message.getChatId());
    }

    private TextHandlerService() {
    }
}
