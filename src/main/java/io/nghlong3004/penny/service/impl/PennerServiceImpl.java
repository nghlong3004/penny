package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.exception.ResourceException;
import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.PennerStatus;
import io.nghlong3004.penny.repository.PennerRepository;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.telegram.TelegramProcessorExecutor;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

@Slf4j
public class PennerServiceImpl implements PennerService {

    private static PennerService instance;
    private final TelegramProcessorExecutor telegramProcessorExecutor;

    @Override
    public Penner getPenner(Long chatId, String firstName, String lastName) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            return pennerRepository.getPennerByChatId(chatId)
                                   .orElseGet(() -> Penner.builder()
                                                          .chatId(chatId)
                                                          .firstName(firstName)
                                                          .lastName(lastName)
                                                          .status(PennerStatus.NOT_LINKED)
                                                          .build());
        } catch (PersistenceException e) {
            telegramProcessorExecutor.executor(chatId, "Có một chút lỗi nhỏ...");
            throw new ResourceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<Penner> getAllPenner() {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            return pennerRepository.getAllPenner().orElseGet(List::of);
        }
    }

    @Override
    public void addPenner(Penner penner) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.insert(penner);
            session.commit();
        }
    }

    @Override
    public void updatePenner(Penner penner) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.update(penner);
            session.commit();
        } catch (PersistenceException e) {
            telegramProcessorExecutor.executor(penner.getChatId(), "Có một chút lỗi nhỏ...");
            throw new ResourceException(e.getLocalizedMessage());
        }
    }

    @Override
    public void deletePenner(Long chatId) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.deletePennerByChatId(chatId);
            session.commit();
        } catch (PersistenceException e) {
            telegramProcessorExecutor.executor(chatId, "Có một chút lỗi nhỏ...");
            throw new ResourceException(e.getLocalizedMessage());
        }
    }

    public static PennerService getInstance() {
        if (instance == null) {
            instance = new PennerServiceImpl();
        }
        return instance;
    }

    private PennerServiceImpl() {
        this.telegramProcessorExecutor = ObjectContainer.getTelegramProcessorExecutorProcessorExecutor();
    }
}
