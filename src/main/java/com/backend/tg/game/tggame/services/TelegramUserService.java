package com.backend.tg.game.tggame.services;

import com.backend.tg.game.tggame.dto.TelegramUserDTO;
import com.backend.tg.game.tggame.models.TelegramUser;
import com.backend.tg.game.tggame.repositories.TelegramUserRepository;
import com.backend.tg.game.tggame.util.TelegramUserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    @Autowired
    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public List<TelegramUser> findAll() {
        return telegramUserRepository.findAll();
    }

    @Transactional(readOnly = false)
    public void save(TelegramUser telegramUser) {
        enrichTelegramUser(telegramUser);

        telegramUserRepository.save(telegramUser);
    }

    public Optional<TelegramUser> getTelegramUserByTgId(Integer id) {
        Optional<TelegramUser> foundTelegramUser = telegramUserRepository.findByTgid(id);
        return foundTelegramUser;
    }

    @Transactional(readOnly = false)
    public void update(TelegramUser updatedTelegramUser) {
        TelegramUser telegramUserToBeUpdated = telegramUserRepository.findByTgid(updatedTelegramUser.getTgid()).get();

        telegramUserToBeUpdated.setIsbot(updatedTelegramUser.getIsbot());
        telegramUserToBeUpdated.setFirstname(updatedTelegramUser.getFirstname());
        telegramUserToBeUpdated.setLastname(updatedTelegramUser.getLastname());
        telegramUserToBeUpdated.setUsername(updatedTelegramUser.getUsername());
        telegramUserToBeUpdated.setLanguageCode(updatedTelegramUser.getLanguageCode());
        telegramUserToBeUpdated.setPremium(updatedTelegramUser.getPremium());
        telegramUserToBeUpdated.setPhotoUrl(updatedTelegramUser.getPhotoUrl());
        telegramUserToBeUpdated.setCreatedAt(updatedTelegramUser.getCreatedAt());



        telegramUserRepository.save(telegramUserToBeUpdated);

    }

    @Transactional(readOnly = false)
    public void delete(int tg_id) {
        TelegramUser foundTelegramUser = getTelegramUserByTgId(tg_id).orElseThrow(TelegramUserNotFoundException::new);
        telegramUserRepository.deleteById(foundTelegramUser.getId());
    }

    private void enrichTelegramUser(TelegramUser telegramUser) {
        telegramUser.setCreatedAt(LocalDateTime.now());
        // устанавливаем дату создания юзера, мб чот ещё в будущем добавим в этот метод для установки каких-то значений на тсороне сервера
        // private т.к. юзается только внутри сервиса. логика никуда не выносится
    }
}