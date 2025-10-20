package io.nghlong3004.penny.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import io.nghlong3004.penny.constant.GoogleConstant;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static io.nghlong3004.penny.constant.GoogleConstant.HTTP_TRANSPORT;

@Slf4j
public class GoogleSheets {
    private final static GoogleSheets INSTANCE = new GoogleSheets();
    private Sheets sheets = null;
    private Credential credential = null;

    public static GoogleSheets getInstance() {
        return INSTANCE;
    }

    public Sheets getSheets() throws IOException {
        if (this.sheets == null) {
            log.info("Building Sheets service...");
            this.sheets = new Sheets.Builder(HTTP_TRANSPORT, GoogleConstant.JSON_FACTORY,
                                             getCredentials(HTTP_TRANSPORT)).setApplicationName(
                    ObjectContainer.getApplication().getApplicationName()).build();
        }
        return this.sheets;
    }

    private GoogleSheets() {
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        if (this.credential == null) {
            log.info("Loading Google Credentials...");
            InputStream in = GoogleSheets.class.getResourceAsStream(GoogleConstant.CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + GoogleConstant.CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GoogleConstant.JSON_FACTORY,
                                                                         new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFLow(clientSecrets);

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(
                    ObjectContainer.getApplication().getApplicationPort()).build();
            this.credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
        return this.credential;
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFLow(GoogleClientSecrets clientSecrets)
            throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, GoogleConstant.JSON_FACTORY, clientSecrets,
                                                       GoogleConstant.SCOPES).setDataStoreFactory(getDataStore())
                                                                             .setAccessType("offline")
                                                                             .build();
    }

    private DataStoreFactory getDataStore() throws IOException {
        return new FileDataStoreFactory(new File(GoogleConstant.TOKENS_DIRECTORY_PATH));
    }
}
