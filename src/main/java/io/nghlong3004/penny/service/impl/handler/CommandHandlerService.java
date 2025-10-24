package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.GifConstant;
import io.nghlong3004.penny.constant.TelegramConstant;
import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.type.CallbackType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import io.nghlong3004.penny.util.GifLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ActionType;
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

    public static HandlerService getInstance() {
        if (instance == null) {
            synchronized (CommandHandlerService.class) {
                if (instance == null) {
                    log.info("Creating new CommandHandlerService instance...");
                    instance = new CommandHandlerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText();
        PennerType status = getStatus(chatId);
        log.debug("Handling command. ChatID: {}, Status: {}, Command: '{}'", chatId, status, text);

        try {
            if (status == PennerType.PENDING) {
                handlePending(message);
                return;
            }
            commands.getOrDefault(text, this::putDefault).accept(chatId);
        } catch (Exception e) {
            log.error("Unhandled exception in command handler. ChatID: {}, Command: '{}'. Error: {}", chatId, text,
                      e.getMessage(), e);
            execute(chatId, "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }

    private void handlePending(Message message) throws ResourceException {
        Long chatId = message.getChatId();
        String text = message.getText();

        log.debug("Handling PENDING command. ChatID: {}, Command: '{}'", chatId, text);

        try {
            if (message.getText().equals(CommandType.SHEETS.getCommand())) {
                commands.get(CommandType.SHEETS.getCommand()).accept(chatId);
            }
            else if (message.getText().equals(CommandType.STEPS_WEB.getCommand())) {
                commands.get(CommandType.STEPS_WEB.getCommand()).accept(chatId);
            }
            else if (message.getText().equals(CommandType.STEPS_PHONE.getCommand())) {
                commands.get(CommandType.STEPS_PHONE.getCommand()).accept(chatId);
            }
            else if (message.getText().equals(CommandType.OUT.getCommand())) {
                commands.get(CommandType.OUT.getCommand()).accept(chatId);
            }
            else {
                log.warn("ChatID: {}. User in PENDING state sent unknown command: '{}'", chatId, text);
                String responseText = FileLoaderUtil.loadFile(CommandType.IN_PENDING.getFilePath());
                execute(chatId, responseText);
                execute(chatId, new Sticker(TelegramConstant.DEFAULT_JPG));
            }
        } catch (Exception e) {
            throw new ResourceException(e.getMessage());
        }
    }

    private CommandHandlerService() {
        commands = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        log.info("Initializing command map...");
        for (CommandType cmd : CommandType.values()) {
            String type = cmd.getCommand();
            switch (cmd) {
                case STEPS_WEB -> commands.put(type, this::putStepWeb);
                case STEPS_PHONE -> commands.put(type, this::putStepPhone);
                case LINK -> commands.put(type, this::putLink);
                case OUT -> commands.put(type, this::putOut);
                case START -> commands.put(type, this::putStart);
                case SHEETS -> commands.put(type, this::putSheets);
                default -> {
                    if (type != null && !type.isBlank()) {
                        String message = FileLoaderUtil.loadFile(cmd.getFilePath());
                        commands.put(type, chatId -> execute(chatId, message));
                    }
                }
            }
        }
        log.info("Command map initialized with {} commands.", commands.size());
    }

    private void putStepPhone(Long chatId) {
        log.info("Executing 'putStepPhone' for ChatID: {}", chatId);
        if (getStatus(chatId) != PennerType.PENDING) {
            log.warn("ChatID: {}. 'putStepPhone' called but status is NOT PENDING. Falling back to default.", chatId);
            putDefault(chatId);
            return;
        }
        for (int i = 1; i <= 11; ++i) {
            execute(chatId, ActionType.UPLOAD_PHOTO);
            String resourcePath = CommandType.STEPS_PHONE.getFilePath() + i + ".jpg";
            execute(chatId, resourcePath, "");
        }
        log.debug("Finished 'putStepPhone' loop for ChatID: {}", chatId);
    }

    private void putSheets(Long chatId) {
        log.info("Executing 'putSheets' for ChatID: {}", chatId);
        if (getStatus(chatId) != PennerType.PENDING) {
            log.warn("ChatID: {}. 'putSheets' called but status is NOT PENDING. Falling back to default.", chatId);
            putDefault(chatId);
            return;
        }
        String message = FileLoaderUtil.loadFile(CommandType.SHEETS.getFilePath());
        execute(chatId, message);
    }

    private void putStart(Long chatId) {
        log.info("Executing 'putStart' for ChatID: {}", chatId);
        String message = FileLoaderUtil.loadFile(CommandType.START.getFilePath());
        message = String.format(message, getFullName(chatId));
        execute(chatId, message);
    }

    private void putOut(Long chatId) {
        log.info("Executing 'putOut' for ChatID: {}", chatId);
        if (getStatus(chatId) == PennerType.PENDING) {
            String message = FileLoaderUtil.loadFile(CommandType.OUT.getFilePath());
            execute(chatId, message);
            execute(chatId, TelegramConstant.PENDING_JPG, "");
            update(chatId, PennerType.NOT_LINKED, null);
            log.info("ChatID: {}. User has exited PENDING state. Status set to NOT_LINKED.", chatId);
        }
        else {
            log.warn("ChatID: {}. 'putOut' called but status is NOT PENDING. Falling back to default.", chatId);
            putDefault(chatId);
        }
    }

    private void putLink(Long chatId) {
        log.info("Executing 'putLink' for ChatID: {}", chatId);
        String message = "Bạn muốn liên kết tới google sheets à?";
        List<Map<String, String>> data = List.of(
                Map.of(CallbackType.YES_LINK.getData(), CallbackType.YES_LINK.getReply(),
                       CallbackType.NO_LINK.getData(), CallbackType.NO_LINK.getReply()));
        execute(chatId, message, data);
    }

    private void putStepWeb(Long chatId) {
        log.info("Executing 'putStep' for ChatID: {}", chatId);
        if (getStatus(chatId) != PennerType.PENDING) {
            log.warn("ChatID: {}. 'putStep' called but status is NOT PENDING. Falling back to default.", chatId);
            putDefault(chatId);
            return;
        }
        for (int i = 1; i <= 7; ++i) {
            execute(chatId, ActionType.UPLOAD_PHOTO);
            String resourcePath = CommandType.STEPS_WEB.getFilePath() + i + ".png";
            execute(chatId, resourcePath, "");
        }
        log.debug("Finished 'putStep' loop for ChatID: {}", chatId);
    }

    private void putDefault(Long chatId) {
        log.warn("Executing 'putDefault' (unknown command) for ChatID: {}", chatId);
        String message = FileLoaderUtil.loadFile(CommandType.DEFAULT.getFilePath());
        execute(chatId, "Có vẻ như bạn gõ sai cú pháp rồi.");
        execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.DEFAULT)).caption(null).build());
        execute(chatId, message);
    }
}
