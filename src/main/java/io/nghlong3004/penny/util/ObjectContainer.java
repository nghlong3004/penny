package io.nghlong3004.penny.util;

import com.google.api.services.sheets.v4.Sheets;
import io.nghlong3004.penny.configuration.ApplicationConfiguration;
import io.nghlong3004.penny.google.GoogleSheets;
import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutor;
import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutorImpl;
import io.nghlong3004.penny.service.HandlerService;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.TransactionParserService;
import io.nghlong3004.penny.service.TransactionService;
import io.nghlong3004.penny.service.impl.GeminiTransactionParser;
import io.nghlong3004.penny.service.impl.PennerServiceImpl;
import io.nghlong3004.penny.service.impl.TransactionServiceImpl;
import io.nghlong3004.penny.service.impl.handler.CallbackHandlerService;
import io.nghlong3004.penny.service.impl.handler.CommandHandlerService;
import io.nghlong3004.penny.service.impl.handler.MessageHandlerService;
import io.nghlong3004.penny.service.impl.handler.TextHandlerService;
import io.nghlong3004.penny.telegram.TelegramApplication;
import io.nghlong3004.penny.telegram.TelegramExecutor;
import org.apache.ibatis.session.SqlSession;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

public final class ObjectContainer {

    private static final ApplicationConfiguration APPLICATION = ApplicationConfiguration.getInstance();

    private static final TelegramClient TELEGRAM_CLIENT = new OkHttpTelegramClient(APPLICATION.getTelegramToken());

    private static final TelegramExecutor TELEGRAM_EXECUTOR = TelegramExecutor.getInstance(TELEGRAM_CLIENT);

    private static TelegramApplication TELEGRAM_APPLICATION;

    private static final PennerService PENNER_SERVICE = PennerServiceImpl.getInstance();

    private static final GoogleSheets GOOGLE_SHEETS = GoogleSheets.getInstance();

    private static final GoogleSheetsProcessorExecutor GOOGLE_SHEETS_PROCESSOR_EXECUTOR = GoogleSheetsProcessorExecutorImpl.getInstance();
    private static final TransactionParserService TRANSACTION_PARSER_SERVICE = GeminiTransactionParser.getInstance();

    private static final TransactionService TRANSACTION_SERVICE = TransactionServiceImpl.getInstance();

    private static final HandlerService COMMAND_HANDLER_SERVICE = CommandHandlerService.getInstance();

    private static final HandlerService TEXT_HANDLER_SERVICE = TextHandlerService.getInstance();

    private static final HandlerService CALLBACK_HANDLER_SERVICE = CallbackHandlerService.getInstance();

    private static final HandlerService MESSAGE_HANDLER_SERVICE = MessageHandlerService.getInstance(
            TEXT_HANDLER_SERVICE, COMMAND_HANDLER_SERVICE, CALLBACK_HANDLER_SERVICE);

    public static TelegramExecutor getTelegramExecutor() {
        return TELEGRAM_EXECUTOR;
    }

    public static TelegramApplication getTelegramApplication() {
        if (TELEGRAM_APPLICATION == null) {
            TELEGRAM_APPLICATION = TelegramApplication.getInstance(APPLICATION.getTelegramToken());
        }
        return TELEGRAM_APPLICATION;
    }

    public static HandlerService getMessageHandlerService() {
        return MESSAGE_HANDLER_SERVICE;
    }

    public static HandlerService getTextHandlerService() {
        return TEXT_HANDLER_SERVICE;
    }

    public static HandlerService getCommandHandlerService() {
        return COMMAND_HANDLER_SERVICE;
    }

    public static TransactionService getTransactionService() {
        return TRANSACTION_SERVICE;
    }

    public static TransactionParserService getTransactionParserService() {
        return TRANSACTION_PARSER_SERVICE;
    }

    public static Sheets getGoogleSheets() throws IOException {
        return GOOGLE_SHEETS.getSheets();
    }

    public static GoogleSheetsProcessorExecutor getGoogleSheetsProcessorExecutor() {
        return GOOGLE_SHEETS_PROCESSOR_EXECUTOR;
    }

    public static SqlSession openSession() {
        return MyBatisUtil.openSession();
    }

    public static PennerService getPennerService() {
        return PENNER_SERVICE;
    }


    public static ApplicationConfiguration getApplication() {
        return APPLICATION;
    }

    private ObjectContainer() {
    }
}
