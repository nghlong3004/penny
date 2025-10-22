package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Penner;

import java.util.List;

public interface PennerService {

    Penner getPenner(Long chatId, String firstName, String lastName);

    List<Penner> getAllPenner();

    void addPenner(Penner penner);

    void updatePenner(Penner penner);

    void deletePenner(Long chatId);

    String getSpreadsheetsId(Long chatId);

}
