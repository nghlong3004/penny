package io.nghlong3004.penny.util;

import io.nghlong3004.penny.configuration.ApplicationConfiguration;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.impl.PennerServiceImpl;
import io.nghlong3004.penny.telegram.TelegramApplication;
import io.nghlong3004.penny.telegram.TelegramProcessorExecutor;
import org.apache.ibatis.session.SqlSession;

public final class ObjectContainer {

    private static final ApplicationConfiguration APPLICATION = ApplicationConfiguration.getInstance();

    private static final PennerService PENNER_SERVICE = PennerServiceImpl.getInstance();

    private static final TelegramProcessorExecutor TELEGRAM_PROCESSOR_EXECUTOR = TelegramProcessorExecutor.getInstance(
            APPLICATION.getTelegramToken());

    private static final TelegramApplication TELEGRAM_APPLICATION = TelegramApplication.getInstance();

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
