package io.nghlong3004.penny.util;

import com.google.api.services.sheets.v4.Sheets;
import io.nghlong3004.penny.configuration.ApplicationConfiguration;
import io.nghlong3004.penny.google.GoogleSheets;
import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutor;
import io.nghlong3004.penny.google.GoogleSheetsProcessorExecutorImpl;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.impl.PennerServiceImpl;
import io.nghlong3004.penny.telegram.TelegramApplication;
import io.nghlong3004.penny.telegram.TelegramProcessorExecutor;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;

public final class ObjectContainer {

    private static final ApplicationConfiguration APPLICATION = ApplicationConfiguration.getInstance();

    private static final PennerService PENNER_SERVICE = PennerServiceImpl.getInstance();

    private static final TelegramProcessorExecutor TELEGRAM_PROCESSOR_EXECUTOR = TelegramProcessorExecutor.getInstance(
            APPLICATION.getTelegramToken());

    private static final TelegramApplication TELEGRAM_APPLICATION = TelegramApplication.getInstance();

    private static final GoogleSheets GOOGLE_SHEETS = GoogleSheets.getInstance();

    private static final GoogleSheetsProcessorExecutor GOOGLE_SHEETS_PROCESSOR_EXECUTOR = GoogleSheetsProcessorExecutorImpl.getInstance();

    public static Sheets getGoogleSheets() throws IOException {
        return GOOGLE_SHEETS.getSheets();
    }

    public static GoogleSheetsProcessorExecutor getGoogleSheetsProcessorExecutor() {
        return GOOGLE_SHEETS_PROCESSOR_EXECUTOR;
    }

    public static SqlSession openSession() {
        return MyBatisUtil.openSession();
    }

    public static TelegramApplication getTelegramApplication() {
        return TELEGRAM_APPLICATION;
    }

    public static PennerService getPennerService() {
        return PENNER_SERVICE;
    }

    public static TelegramProcessorExecutor getTelegramProcessorExecutorProcessorExecutor() {
        return TELEGRAM_PROCESSOR_EXECUTOR;
    }


    public static ApplicationConfiguration getApplication() {
        return APPLICATION;
    }

    private ObjectContainer() {
    }
}
