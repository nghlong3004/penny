package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.service.PennerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PennerServiceImpl implements PennerService {

    private static PennerService instance;

    @Override
    public Penner getPennerByChatId(Long chatId, String firstName, String lastName) {
        return null;
    }

    public static PennerService getInstance() {
        if (instance == null) {
            instance = new PennerServiceImpl();
        }
        return instance;
    }

    private PennerServiceImpl() {
    }
}
