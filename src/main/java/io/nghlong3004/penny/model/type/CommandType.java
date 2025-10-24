package io.nghlong3004.penny.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    START("/start", "command/start.html"),
    STEPS_WEB("/steps_web", "images/step_"),
    STEPS_PHONE("/steps_phone", "images/step_"),
    SHEETS("/sheets_guide", "command/sheets_guide.html"),
    DEFAULT("", "command/help.html"),
    LINK("/link", "command/link.html"),
    OUT("/out", "command/out.html"),
    TIPS("/tips", "command/tips.html"),
    IN_PENDING("", "command/in_pending.html"),
    DAILY("/today", "Tổng kết hôm nay:"),
    WEEKLY("/week", "Tổng kết 7 ngày qua:"),
    MONTHLY("/month", "Tổng kết tháng này:"),
    FIND("/find", ""),
    UNDO("/undo", ""),
    OFF("/off", ""),
    HELP("/help", "command/help.html");
    private final String command;
    private final String detail;

}
