package io.nghlong3004.penny.google;

import com.google.api.services.sheets.v4.model.*;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GoogleSheetsProcessorExecutorImpl implements GoogleSheetsProcessorExecutor {

    private static final GoogleSheetsProcessorExecutor INSTANCE = new GoogleSheetsProcessorExecutorImpl();

    public static GoogleSheetsProcessorExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    public String readFromSheet(String spreadsheetsId, String range) throws IOException {
        log.debug("Reading data from Google Sheets. ID={}, range={}", spreadsheetsId, range);
        try {
            ValueRange response = ObjectContainer.getGoogleSheets()
                                                 .spreadsheets()
                                                 .values()
                                                 .get(spreadsheetsId, range)
                                                 .execute();
            List<List<Object>> values = response.getValues();
            log.info("Reading data successfully from , ID={}, size rows={}", spreadsheetsId, values.size());
            return parseObjectToString(values);
        } catch (IOException e) {
            log.error("IOException read Google Sheets. ID: {}, Range: {}", spreadsheetsId, range, e);
            throw e;
        }
    }

    @Override
    public boolean writeToSheet(String spreadsheetsId, String range, List<List<Object>> data) throws IOException {
        log.debug("Writing {} rows of data to Google Sheets. ID: {}, Range: {}", data.size(), spreadsheetsId, range);
        try {
            ValueRange body = new ValueRange().setValues(data);
            UpdateValuesResponse result = ObjectContainer.getGoogleSheets()
                                                         .spreadsheets()
                                                         .values()
                                                         .update(spreadsheetsId, range, body)
                                                         .setValueInputOption("USER_ENTERED")
                                                         .execute();
            log.info("Successfully updated {} cells in Sheet ID: {}, Range: {}", result.getUpdatedCells(),
                     spreadsheetsId, range);
            return true;
        } catch (IOException e) {
            log.error("Failed to write to Google Sheets", e);
            throw e;
        }
    }

    @Override
    public boolean insertRowAbove(String spreadsheetsId, String sheetName, int rowIndex) throws IOException {
        log.debug("Attempting to insert new row above rowIndex={} in sheetName={} (ID: {})", rowIndex, sheetName,
                  spreadsheetsId);
        try {
            Integer sheetId = getSheetIdByName(spreadsheetsId, sheetName);
            if (sheetId == null) {
                log.warn("Sheet name {} not found in Spreadsheet ID: {}", sheetName, spreadsheetsId);
                return false;
            }
            DimensionRange dimRange = new DimensionRange().setSheetId(sheetId)
                                                          .setDimension("ROWS")
                                                          .setStartIndex(rowIndex)
                                                          .setEndIndex(rowIndex + 1);
            InsertDimensionRequest insertReq = new InsertDimensionRequest().setRange(dimRange)
                                                                           .setInheritFromBefore(rowIndex > 0);
            Request request = new Request().setInsertDimension(insertReq);
            BatchUpdateSpreadsheetRequest batchReq = new BatchUpdateSpreadsheetRequest().setRequests(
                    Collections.singletonList(request));
            ObjectContainer.getGoogleSheets().spreadsheets().batchUpdate(spreadsheetsId, batchReq).execute();
            log.info("Successfully inserted row above rowIndex={} in sheetName={}", rowIndex, sheetName);
            return true;
        } catch (IOException e) {
            log.error("Failed to insert row in Sheet ID: {}, Name: {}. Error: {}", spreadsheetsId, sheetName,
                      e.getMessage(), e);
            throw e;
        }
    }

    private Integer getSheetIdByName(String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = ObjectContainer.getGoogleSheets()
                                                 .spreadsheets()
                                                 .get(spreadsheetId)
                                                 .setFields("sheets.properties")
                                                 .execute();
        for (Sheet sheet : spreadsheet.getSheets()) {
            if (sheet.getProperties().getTitle().equalsIgnoreCase(sheetName)) {
                return sheet.getProperties().getSheetId();
            }
        }
        return null;
    }

    private String parseObjectToString(List<List<Object>> values) {
        return values.stream()
                     .map(value -> value.stream()
                                        .map(Object::toString)
                                        .map(String::trim)
                                        .collect(Collectors.joining(" ")))
                     .collect(Collectors.joining("\n"));
    }

    private GoogleSheetsProcessorExecutorImpl() {
    }

}
