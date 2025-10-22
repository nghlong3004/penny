package io.nghlong3004.penny.model;

import io.nghlong3004.penny.model.type.PennerType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Penner {
    private Long id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String spreadsheetsId;
    private PennerType status;
    private Timestamp updated;
    private Timestamp created;
}
