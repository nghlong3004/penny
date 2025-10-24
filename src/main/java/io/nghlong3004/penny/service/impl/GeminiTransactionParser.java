package io.nghlong3004.penny.service.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.model.AIResponse;
import io.nghlong3004.penny.service.TransactionParserService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeminiTransactionParser extends TransactionParserService {

    @Getter
    private static final TransactionParserService instance = new GeminiTransactionParser();
    private final String apiKey;
    private final String model;

    @Override
    public AIResponse parser(String rawMessage) throws ResourceException {
        String rawText = ask(rawMessage, 4);
        String json = parserJson(rawText);

        return parserAIResponse(json);
    }

    @Override
    public String ask(String rawMessage, int retry) throws ResourceException {
        try (Client client = Client.builder().apiKey(apiKey).build()) {
            String message = buildPrompt(rawMessage);
            GenerateContentResponse response = client.models.generateContent(model, message, null);
            String rawText = response.text();
            log.debug(rawText);
            return rawText;
        } catch (Exception e) {
            if (retry > 0) {
                log.info("Retry time={}", retry);
                return ask(rawMessage, --retry);
            }
            else {
                throw new ResourceException(e.getLocalizedMessage());
            }
        }
    }

    private GeminiTransactionParser() {
        super();
        apiKey = ObjectContainer.getApplication().getGoogleAIToken();
        model = ObjectContainer.getApplication().getGoogleAIModel();
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key not empty!");
        }
        if (model.isEmpty()) {
            throw new IllegalArgumentException("Model Gemini AI not empty!");
        }
    }
}
