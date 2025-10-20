package io.nghlong3004.penny.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nghlong3004.penny.constant.GoogleConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class GoogleCredentialReader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static JsonNode credentialData;

    static {
        try {
            InputStream in = GoogleCredentialReader.class.getResourceAsStream(GoogleConstant.CREDENTIALS_FILE_PATH);
            credentialData = OBJECT_MAPPER.readTree(in).get("installed");
            log.info("Loaded Google OAuth credentials from credentials.json");
        } catch (Exception e) {
            log.error("Failed to load credentials.json", e);
            throw new RuntimeException(e);
        }
    }

    public static String getClientId() {
        return credentialData.get("client_id").asText();
    }

    public static String getClientSecret() {
        return credentialData.get("client_secret").asText();
    }

    public static String getRedirectUri() {
        return credentialData.get("redirect_uris").get(0).asText();
    }
}
