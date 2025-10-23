package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.GifConstant;
import io.nghlong3004.penny.constant.TelegramConstant;
import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.CallbackType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import io.nghlong3004.penny.util.GifLoaderUtil;
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
        else if (message.getText().equals(CommandType.OUT.getCommand())) {
            commands.get(CommandType.OUT.getCommand()).accept(chatId);
        }
        else {
            String text = FileLoaderUtil.loadFile(CommandType.IN_PENDING.getFilePath());
            execute(message.getChatId(), text);
            execute(message.getChatId(), new Sticker(TelegramConstant.DEFAULT_JPG));
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
            String type = cmd.getCommand();
            switch (cmd) {
                case STEPS -> commands.put(type, this::putStep);
                case LINK -> commands.put(type, this::putLink);
                case OUT -> commands.put(type, this::putOut);
                case START -> commands.put(type, this::putStart);
                case SHEETS -> commands.put(type, this::putSheets);
                default -> {
                    if (!type.isBlank()) {
                        String message = FileLoaderUtil.loadFile(cmd.getFilePath());
                        commands.put(type, chatId -> execute(chatId, message));
                    }
                }
            }
        }
    }

    private void putSheets(Long chatId) {
        if (getStatus(chatId) != PennerType.PENDING) {
            putDefault(chatId);
            return;
        }
        String message = FileLoaderUtil.loadFile(CommandType.SHEETS.getFilePath());
        execute(chatId, message);
    }

    private void putStart(Long chatId) {
        String message = FileLoaderUtil.loadFile(CommandType.START.getFilePath());
        message = String.format(message, getFullName(chatId));
        execute(chatId, message);
    }

    private void putOut(Long chatId) {
        if (getStatus(chatId) == PennerType.PENDING) {
            String message = FileLoaderUtil.loadFile(CommandType.OUT.getFilePath());
            execute(chatId, message);
            execute(chatId, TelegramConstant.PENDING_JPG, "");
            updateStatus(chatId, PennerType.NOT_LINKED);
        }
        else {
            putDefault(chatId);
        }
    }

    private void putLink(Long chatId) {
        String message = "Bạn muốn liên kết tới google sheets à?";
        List<Map<String, String>> data = List.of(
                Map.of(CallbackType.YES_LINK.getData(), CallbackType.YES_LINK.getReply(),
                       CallbackType.NO_LINK.getData(), CallbackType.NO_LINK.getReply()));
        execute(chatId, message, data);
    }

    private void putStep(Long chatId) {
        if (getStatus(chatId) != PennerType.PENDING) {
            putDefault(chatId);
            return;
        }
        for (int i = 1; i <= 7; ++i) {
            String caption = "Đây là bước thứ " + i;
            String resourcePath = CommandType.STEPS.getFilePath() + i + ".png";
            execute(chatId, resourcePath, caption);
        }
    }

    private void putDefault(Long chatId) {
        String message = FileLoaderUtil.loadFile(CommandType.DEFAULT.getFilePath());
        execute(chatId, "Có vẻ như bạn gõ sai cú pháp rồi.");
        execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.DEFAULT)).caption(null).build());
        execute(chatId, message);
    }
}
