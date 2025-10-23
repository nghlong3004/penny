package io.nghlong3004.penny.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleSheetsUtil {

    private static final Pattern GOOGLE_SHEET_LINK_PATTERN = Pattern.compile(
            "https://docs\\.google\\.com/spreadsheets/d/([a-zA-Z0-9-_]+)");

    public static Optional<String> extractSpreadsheetId(String messageText) {
        Matcher matcher = GOOGLE_SHEET_LINK_PATTERN.matcher(messageText);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

}
