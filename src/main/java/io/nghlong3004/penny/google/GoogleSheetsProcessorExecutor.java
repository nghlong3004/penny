package io.nghlong3004.penny.google;

import java.util.List;

public interface GoogleSheetsProcessorExecutor {

    String readFromSheet(String spreadsheetsId, String range);

    boolean writeToSheet(String spreadsheetsId, String range, List<List<Object>> data);

    boolean insertRowAbove(String spreadsheetsId, String sheetId, int rowIndex);

}
