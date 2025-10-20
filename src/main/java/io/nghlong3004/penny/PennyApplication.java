package io.nghlong3004.penny;

import io.nghlong3004.penny.telegram.TelegramApplication;
import io.nghlong3004.penny.util.ObjectContainer;

public class PennyApplication {
    public static void main(String[] args) {
        TelegramApplication application = ObjectContainer.getTelegramApplication();
        application.run();
    }
}