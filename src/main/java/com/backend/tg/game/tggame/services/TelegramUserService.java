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
        Optional<TelegramUser> foundTelegramUser = telegramUserRepository.findByTelegramId(id);
        return foundTelegramUser;
    }

    @Transactional(readOnly = false)
    public void update(TelegramUser telegramUser) {
        TelegramUser updatedTelegramUser = telegramUserRepository.findByTelegramId(telegramUser.getId()).get();

//        System.out.println("updatedTelegramUser.getCreatedAt(): " + updatedTelegramUser.getCreatedAt());
//        System.out.println("telegramUser.getCreatedAt(): " + telegramUser.getCreatedAt());

        // createdAt, id - нет смысла обновлять

        updatedTelegramUser.setFirst_name(telegramUser.getFirst_name());
        updatedTelegramUser.setLast_name(telegramUser.getLast_name());
        updatedTelegramUser.setUsername(telegramUser.getUsername());
        updatedTelegramUser.setLanguage_code(telegramUser.getLanguage_code());
        updatedTelegramUser.setFirst_name(telegramUser.getQuery_id());
        updatedTelegramUser.setHash(telegramUser.getHash());
        updatedTelegramUser.setAuth_date(telegramUser.getAuth_date());

        // Эти данные неизвестны. Можно попробовать делать апи запрос к телеге и получать их отдельно.
        // updatedTelegramUser.setIs_premium(telegramUser.getIs_premium());
        // updatedTelegramUser.setPhoto_url(telegramUser.getPhoto_url());

        // createdAt при обновлении всё равно обновляется на null, хотя в коде нигде не обновляю

        telegramUserRepository.save(updatedTelegramUser);
    }

    @Transactional(readOnly = false)
    public void delete(int id) {
        TelegramUser foundTelegramUser = getTelegramUserByTgId(id).orElseThrow(TelegramUserNotFoundException::new);
        telegramUserRepository.deleteById(foundTelegramUser.getUser_id());
    }

    private void enrichTelegramUser(TelegramUser telegramUser) {
        telegramUser.setCreatedAt(LocalDateTime.now());
        // устанавливаем дату создания юзера, мб чот ещё в будущем добавим в этот метод для установки каких-то значений на тсороне сервера
        // private т.к. юзается только внутри сервиса. логика никуда не выносится
    }
}
