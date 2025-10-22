package io.nghlong3004.penny.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class InlineKeyboardUtil {

    public static InlineKeyboardButton button(String data, String reply) {
        return InlineKeyboardButton.builder().text(data).callbackData(reply).build();
    }

    public static InlineKeyboardMarkup markup(List<List<InlineKeyboardButton>> datas) {

        List<InlineKeyboardRow> rows = datas.stream().map(buttons -> {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.addAll(buttons);
            return row;
        }).toList();
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup structure(List<Map<String, String>> keyboardData) {
        List<List<InlineKeyboardButton>> keyboards = keyboardData.stream()
                                                                 .map(row -> row.entrySet()
                                                                                .stream()
                                                                                .sorted(Map.Entry.comparingByKey())
                                                                                .map(e -> button(e.getKey(),
                                                                                                 e.getValue()))
                                                                                .toList())
                                                                 .toList();

        return markup(keyboards);
    }

    public static InlineKeyboardMarkup createKeyboard(List<Map<String, String>> keyboardData) {
        if (keyboardData == null || keyboardData.isEmpty()) {
            return InlineKeyboardMarkup.builder().keyboard(Collections.emptyList()).build();
        }

        List<List<InlineKeyboardButton>> keyboard = keyboardData.stream()
                                                                .filter(Objects::nonNull)
                                                                .map(rowMap -> rowMap.entrySet()
                                                                                     .stream()
                                                                                     .map(e -> button(e.getKey(),
                                                                                                      e.getValue()))
                                                                                     .collect(Collectors.toList()))
                                                                .collect(Collectors.toList());

        return markup(keyboard);
    }

    private InlineKeyboardUtil() {

    }
}
