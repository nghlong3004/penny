package io.nghlong3004.penny.constant;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Collections;
import java.util.List;

public final class GoogleConstant {

    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static final String OAUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    public static NetHttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

}
