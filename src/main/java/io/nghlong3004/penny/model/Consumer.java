package io.nghlong3004.penny.model;

import io.nghlong3004.penny.model.type.PennerType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Consumer {
    private PennerType status;
    private String firstName;
    private String lastName;
}
