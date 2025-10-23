package io.nghlong3004.penny.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Animation {
    private String url;
    private String caption;
}
