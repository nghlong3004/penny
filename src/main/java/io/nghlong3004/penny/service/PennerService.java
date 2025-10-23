package io.nghlong3004.penny.service;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.type.PennerType;

import java.util.List;

public interface PennerService {

    Penner getPenner(Long chatId, String firstName, String lastName);

    List<Penner> getAllPenner();

    void addPenner(Penner penner);

    void updatePenner(Penner penner);

    void updatePenner(Long chatId, PennerType status);

    void deletePenner(Long chatId);

    String getSpreadsheetsId(Long chatId);

}
