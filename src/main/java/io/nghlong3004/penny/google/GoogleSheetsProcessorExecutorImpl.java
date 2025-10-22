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
    public String readFromSheet(String spreadsheetsId, String range) {
        log.debug("Reading data from Google Sheets");
        try {
            ValueRange response = ObjectContainer.getGoogleSheets()
                                                 .spreadsheets()
                                                 .values()
                                                 .get(spreadsheetsId, range)
                                                 .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                throw new RuntimeException("Data not found");
            }
            return parseObjectToString(values);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean writeToSheet(String spreadsheetsId, String range, List<List<Object>> data) {
        log.debug("Write data to Google Sheets");
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No data to write");
        }
        try {
            ValueRange body = new ValueRange().setValues(data);
            UpdateValuesResponse result = ObjectContainer.getGoogleSheets()
                                                         .spreadsheets()
                                                         .values()
                                                         .update(spreadsheetsId, range, body)
                                                         .setValueInputOption("USER_ENTERED")
                                                         .execute();
            log.debug("{} cells updated successfully", result.getUpdatedCells());
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insertRowAbove(String spreadsheetsId, String sheetName, int rowIndex) {
        log.debug("Inserting new row above rowIndex={} in sheetName={}", rowIndex, sheetName);
        try {
            Integer sheetId = getSheetIdByName(spreadsheetsId, sheetName);
            if (sheetId == null) {
                throw new RuntimeException("Sheet name" + sheetName + "not found");
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
            log.debug("Inserted row successfully.");
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error inserting row", e);
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
