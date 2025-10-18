package io.nghlong3004.penny.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Penner {
    private Long id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private PennerStatus status;
    private LocalDateTime updated;
    private LocalDateTime created;
}
