package io.nghlong3004.penny.util;

import io.nghlong3004.penny.configuration.ApplicationConfiguration;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.service.PennyBotService;
import io.nghlong3004.penny.service.PennyService;
import io.nghlong3004.penny.service.impl.PennerServiceImpl;
import io.nghlong3004.penny.service.impl.PennyBotServiceImpl;
import io.nghlong3004.penny.service.impl.PennyServiceImpl;
import org.apache.ibatis.session.SqlSession;

public final class ObjectContainer {

    private static final ApplicationConfiguration APPLICATION = ApplicationConfiguration.getInstance();

    private static final PennyService PENNY_SERVICE = PennyServiceImpl.getInstance();

    private static final PennerService PENNER_SERVICE = PennerServiceImpl.getInstance();

    private static final PennyBotService PENNY_BOT_SERVICE = PennyBotServiceImpl.getInstance(
            APPLICATION.getTelegramToken());

    public static SqlSession openSession() {
        return MyBatisUtil.openSession();
    }

    public static PennyBotService getPennyBotService() {
        return PENNY_BOT_SERVICE;
    }

    public static PennerService getPennerService() {
        return PENNER_SERVICE;
    }

    public static PennyService getPennyService() {
        return PENNY_SERVICE;
    }


    public static ApplicationConfiguration getApplication() {
        return APPLICATION;
    }

    private ObjectContainer() {
    }
}
