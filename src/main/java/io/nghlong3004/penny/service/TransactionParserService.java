package io.nghlong3004.penny.service;

import com.google.gson.Gson;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.util.FileLoaderUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class TransactionParserService {

    private static final Pattern JSON_CLEANER_PATTERN = Pattern.compile("```json\\s*([\\s\\S]+?)\\s*```",
                                                                        Pattern.CASE_INSENSITIVE);

    private final Gson gson;

    public abstract AIResponse parser(String rawMessage);

    public abstract String ask(String rawMessage);

    protected String buildPrompt(String rawMessage) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return String.format(FileLoaderUtil.loadFile("prompt_template.txt"), currentDate, rawMessage);
    }

    protected String parserJson(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "{}";
        }
        String trimmedText = rawText.trim();
        Matcher matcher = JSON_CLEANER_PATTERN.matcher(trimmedText);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        else {
            return trimmedText;
        }
    }

    protected AIResponse parserAIResponse(String json) {
        return gson.fromJson(json, AIResponse.class);
    }

    protected TransactionParserService() {
        gson = new Gson();
    }

}
