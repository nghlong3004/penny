package io.nghlong3004.penny.google;

import java.io.IOException;
import java.util.List;

public interface GoogleSheetsProcessorExecutor {

    String readFromSheet(String spreadsheetsId, String range) throws IOException;

    boolean writeToSheet(String spreadsheetsId, String range, List<List<Object>> data) throws IOException;

    boolean insertRowAbove(String spreadsheetsId, String sheetId, int rowIndex) throws IOException;

}
