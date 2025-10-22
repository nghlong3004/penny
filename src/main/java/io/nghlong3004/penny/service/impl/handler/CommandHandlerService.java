package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.TelegramConstant;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.CallbackType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class CommandHandlerService extends HandlerService {

    private static volatile HandlerService instance;
    private final Map<String, Consumer<Long>> commands;

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        if (getStatus(message.getChatId()) == PennerType.PENDING) {
            handlePending(message);
            return;
        }
        commands.getOrDefault(message.getText(), this::putDefault).accept(message.getChatId());
    }

    private void handlePending(Message message) {
        Long chatId = message.getChatId();
        if (message.getText().equals(CommandType.SHEETS.getCommand())) {
            commands.get(CommandType.SHEETS.getCommand()).accept(chatId);
        }
        else if (message.getText().equals(CommandType.STEPS.getCommand())) {
            commands.get(CommandType.STEPS.getCommand()).accept(chatId);
        }
        else {
            execute(message.getChatId(), "Mình đang chờ bạn làm xong rồi gửi liên kết google sheets đây...");
            execute(message.getChatId(), new Sticker(TelegramConstant.DEFAULT_JPG));
            execute(message.getChatId(), "Trong quá trình này bạn có thể gõ /sheets_guide hoặc /steps");
        }
    }

    public static HandlerService getInstance() {
        if (instance == null) {
            synchronized (CommandHandlerService.class) {
                if (instance == null) {
                    instance = new CommandHandlerService();
                }
            }
        }
        return instance;
    }

    private CommandHandlerService() {
        commands = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        for (CommandType cmd : CommandType.values()) {
            switch (cmd) {
                case STEPS -> commands.put(cmd.getCommand(), this::putStep);
                case LINK -> putLink();
                default -> {
                    String message = FileLoaderUtil.loadFile(cmd.getFilePath());
                    commands.put(cmd.getCommand(), chatId -> execute(chatId, message));
                }
            }
        }
    }

    private void putLink() {
        commands.put(CommandType.LINK.getCommand(), chatId -> {
            String message = "Bạn muốn liên kết tới google sheets à?";
            List<Map<String, String>> data = List.of(
                    Map.of(CallbackType.YES_LINK.getData(), CallbackType.YES_LINK.getReply(),
                           CallbackType.NO_LINK.getData(), CallbackType.NO_LINK.getReply()));
            execute(chatId, message, data);
        });
    }

    private void putStep(Long chatId) {
        for (int i = 1; i <= 7; ++i) {
            String caption = "Đây là bước thứ " + i;
            String resourcePath = CommandType.STEPS.getFilePath() + i + ".png";
            execute(chatId, resourcePath, caption);
        }
    }

    private void putDefault(Long chatId) {
        String message = FileLoaderUtil.loadFile(CommandType.DEFAULT.getFilePath());
        execute(chatId, "Có vẻ như bạn gõ sai cú pháp rồi.");
        execute(chatId, new Sticker(TelegramConstant.DEFAULT_JPG));
        execute(chatId, message);
    }
}
