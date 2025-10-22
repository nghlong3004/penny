package io.nghlong3004.penny.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallbackType {
    YES_LINK("Đồng ý", "Okay."),
    NO_LINK("Không má, ấn lộn", "Được thôi, hãy suy nghĩ lại và gõ /link nếu cần.");

    private final String data;
    private final String reply;
}
