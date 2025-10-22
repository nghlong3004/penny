package io.nghlong3004.penny.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    START("/start", "command/start.txt"),
    STEPS("/steps", "images/step_"),
    SHEETS("/sheets_guide", "command/sheets_guide.txt"),
    DEFAULT("", "command/default.txt"),
    LINK("/link", "command/link.txt"),
    HELP("/help", "command/help.txt");
    private final String command;
    private final String filePath;

}
