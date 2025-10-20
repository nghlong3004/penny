package io.nghlong3004.penny.google;

import java.util.List;

public interface GoogleSheetsProcessorExecutor {

    String readFromSheet(String spreadsheetId, String range);

    boolean writeToSheet(String spreadsheetId, String range, List<List<Object>> data);

    boolean insertRowAbove(String spreadsheetId, String sheetId, int rowIndex);

}
