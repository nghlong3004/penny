package io.nghlong3004.penny.service.impl.handler;

import io.nghlong3004.penny.constant.GifConstant;
import io.nghlong3004.penny.constant.TelegramConstant;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.model.Animation;
import io.nghlong3004.penny.model.Sticker;
import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.type.ColumnType;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.model.type.TransactionType;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.service.TransactionParserService;
import io.nghlong3004.penny.service.TransactionService;
import io.nghlong3004.penny.util.FileLoaderUtil;
import io.nghlong3004.penny.util.GifLoaderUtil;
import io.nghlong3004.penny.util.GoogleSheetsUtil;
import io.nghlong3004.penny.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TextHandlerService extends HandlerService {

    private static volatile HandlerService instance;
    private final TransactionParserService parserService;
    private final TransactionService transactionService;

    public static HandlerService getInstance(TransactionParserService parserService,
                                             TransactionService transactionService) {
        if (instance == null) {
            synchronized (TextHandlerService.class) {
                if (instance == null) {
                    log.info("Creating new TextHandlerService instance...");
                    instance = new TextHandlerService(parserService, transactionService);
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        PennerType status = getStatus(chatId);
        log.debug("Handling text message for ChatID: {}. Status: {}. Message: '{}'", chatId, status, message.getText());
        if (status == PennerType.PENDING) {
            handlePendingLogic(message);
        }
        else {
            handleTextMessage(message);
        }

    }

    private void handlePendingLogic(Message message) {
        Long chatId = message.getChatId();
        Optional<String> extractedId = GoogleSheetsUtil.extractSpreadsheetId(message.getText());

        if (extractedId.isPresent()) {
            log.debug("ChatID: {}. User in PENDING provided a potential spreadsheet ID.", chatId);
            handleLinkingStatus(chatId, extractedId.get());
        }
        else {
            log.debug("ChatID: {}. User in PENDING sent non-ID text.", chatId);
            String text = FileLoaderUtil.loadFile(CommandType.IN_PENDING.getDetail());
            execute(chatId, text);
            execute(chatId, new Sticker(TelegramConstant.DEFAULT_JPG));
        }
    }

    private void handleTextMessage(Message message) {
        String prompt = FileLoaderUtil.loadFile("prompt_template.txt");
        String localDate = LocalDate.now().toString();
        String rawMessage = String.format(prompt, localDate, message.getText());
        Long chatId = message.getChatId();

        log.debug("ChatID: {}. Sending to AI parser. Raw: {}", chatId, rawMessage);
        AIResponse aiResponse;
        try {
            aiResponse = parserService.parser(rawMessage);
            log.debug("ChatID: {}. Received AI response. Error: {}", chatId, aiResponse.error());
        } catch (Exception e) {
            log.error("ChatID: {}. AI Parser service failed! Error: {}", chatId, e.getMessage(), e);
            execute(chatId, "Lỗi: Không thể phân tích tin nhắn của bạn. Dịch vụ AI có thể đang gặp sự cố.");
            execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.SAD)).caption(null).build());
            return;
        }
        if (aiResponse.error() != null && !aiResponse.error().isBlank()) {
            log.warn("ChatID: {}. AI parser returned a functional error: {}", chatId, aiResponse.error());
            execute(chatId, aiResponse.reply());
            execute(chatId, Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.SAD)).caption(null).build());
        }
        else {
            handleTransaction(aiResponse, chatId);
        }
    }

    private void handleTransaction(AIResponse aiResponse, Long chatId) {
        log.info("ChatID: {}. Handling valid transaction: {}", chatId, aiResponse.toString());

        if (getStatus(chatId) == PennerType.LINKED) {
            if (!attemptWriteToSheet(chatId, aiResponse)) {
                log.warn("ChatID: {}. Failed to write to Google Sheets after retries.", chatId);
                execute(chatId, "Mình không ghi ra google sheets của bạn được, xin vui lòng thử lại");
                execute(chatId,
                        Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.SAD)).caption(null).build());
                return;
            }
        }

        try {
            transactionService.add(chatId, aiResponse);
            log.info("ChatID: {}. Transaction added to local DB successfully.", chatId);
            execute(chatId, aiResponse.reply());
            execute(chatId,
                    Animation.builder().url(GifLoaderUtil.getRandomUrl(GifConstant.HAPPY)).caption(null).build());
        } catch (Exception e) {
            log.error("ChatID: {}. CRITICAL! Failed to add transaction to LOCAL DB! Error: {}", chatId, e.getMessage(),
                      e);
            execute(chatId, "Có một vài lỗi nhỏ ở phía máy chủ!");
        }
    }

    private boolean attemptWriteToSheet(Long chatId, AIResponse aiResponse) {
        ColumnType columnType = (aiResponse.type() == TransactionType.INCOME) ? ColumnType.INCOME : ColumnType.EXPENSE;
        int retries = 4;
        while (retries > 0) {
            try {
                String spreadsheetId = getSpreadsheetsId(chatId);
                log.debug("ChatID: {}. Attempting to write to sheet. Attempt {}/4", chatId, (5 - retries));
                transactionService.write(spreadsheetId, columnType, aiResponse);
                log.info("ChatID: {}. Successfully wrote transaction to Google Sheets.", chatId);
                return true;
            } catch (Exception e) {
                log.warn("ChatID: {}. Exception during Google Sheets write attempt. Retries left: {}. Error: {}",
                         chatId, retries - 1, e.getMessage());
            }
            retries--;
        }

        log.error("ChatID: {}. Failed to write to Google Sheets after 4 attempts.", chatId);
        return false;
    }

    private void handleLinkingStatus(Long chatId, String spreadsheetId) {
        log.info("ChatID: {}. Attempting to link spreadsheet ID: {}", chatId, spreadsheetId);
        execute(chatId, "Đã nhận link. Mình đang kiểm tra và đồng bộ, bạn đợi xíu nhé...");
        ThreadPoolUtil.BACKGROUND_EXECUTOR.submit(() -> {
            try {
                if (transactionService.isSheetWriteable(spreadsheetId)) {
                    execute(chatId, "Link bạn gửi có vẻ ổn rồi, giờ mình sẽ đồng bộ dữ liệu xuống...");
                    update(chatId, PennerType.LINKED, spreadsheetId);
                    handleSyncDatabaseToSheets(chatId, spreadsheetId);
                    execute(chatId, "Đồng bộ đã xong, chúc bạn vui vẻ!");
                    log.info("ChatID: {}. Spreadsheet linked successfully.", chatId);
                }
                else {
                    execute(chatId,
                            "Bạn hãy làm rồi gửi lại cho mình nhé, mình chưa truy cập được vào google sheets của bạn!");
                    log.warn("ChatID: {}. Spreadsheet linking FAILED (not readable).", chatId);
                }
            } catch (Exception e) {
                log.error("ChatID: {}. Exception during 'isSheetReadable' check for Sheet ID: {}. Error: {}", chatId,
                          spreadsheetId, e.getMessage(), e);
                execute(chatId,
                        "Đã có lỗi xảy ra khi cố gắng truy cập Google Sheet của bạn. Vui lòng kiểm tra lại ID và quyền truy cập.");
            }
        });
    }

    private void handleSyncDatabaseToSheets(Long chatId, String spreadsheetId) throws RuntimeException {
        List<Transaction> transactions = transactionService.getAllTransactionByChatId(chatId);
        transactions.forEach(transaction -> {
            ColumnType columnType =
                    (transaction.type() == TransactionType.INCOME) ? ColumnType.INCOME : ColumnType.EXPENSE;
            log.info(transaction.dated().toString());
            AIResponse aiResponse = new AIResponse(transaction.description(), transaction.amount(), transaction.type(),
                                                   transaction.dated().toString().split(" ")[0], null, null);
            try {
                transactionService.write(spreadsheetId, columnType, aiResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private TextHandlerService(TransactionParserService parserService, TransactionService transactionService) {
        this.parserService = parserService;
        this.transactionService = transactionService;
    }
}
