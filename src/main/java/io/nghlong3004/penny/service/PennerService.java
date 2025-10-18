package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Penner;

public interface PennerService {

    Penner getPennerByChatId(Long chatId, String firstName, String lastName);

}
