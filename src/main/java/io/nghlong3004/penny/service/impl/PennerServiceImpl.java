package io.nghlong3004.penny.service.impl;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.type.PennerType;
import io.nghlong3004.penny.repository.PennerRepository;
import io.nghlong3004.penny.service.PennerService;
import io.nghlong3004.penny.util.ObjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;

@Slf4j
public class PennerServiceImpl implements PennerService {

    private static PennerService instance;

    @Override
    public Penner getPenner(Long chatId, String firstName, String lastName) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            return pennerRepository.getPennerByChatId(chatId)
                                   .orElseGet(() -> Penner.builder()
                                                          .chatId(chatId)
                                                          .firstName(firstName)
                                                          .lastName(lastName)
                                                          .status(PennerType.NOT_LINKED)
                                                          .build());
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public List<Penner> getAllPenner() {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            return Optional.of(pennerRepository.getAllPenner()).orElseGet(List::of);
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public void addPenner(Penner penner) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.insert(penner);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Override
    public void updatePenner(Penner penner) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.update(penner);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Override
    public void deletePenner(Long chatId) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            pennerRepository.deletePennerByChatId(chatId);
            session.commit();
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Override
    public String getSpreadsheetsId(Long chatId) {
        try (SqlSession session = ObjectContainer.openSession()) {
            PennerRepository pennerRepository = session.getMapper(PennerRepository.class);
            return pennerRepository.getSpreadsheetsId(chatId);
        } catch (PersistenceException e) {
            log.debug(e.getLocalizedMessage());
            return null;
        }
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
