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
    public void update(TelegramUser updatedTelegramUser) {
        TelegramUser telegramUserToBeUpdated = telegramUserRepository.findByTelegramId(updatedTelegramUser.getId()).get();


        telegramUserToBeUpdated.setQuery_id(updatedTelegramUser.getQuery_id());
        telegramUserToBeUpdated.setId(updatedTelegramUser.getId());
        telegramUserToBeUpdated.setFirst_name(updatedTelegramUser.getFirst_name());
        telegramUserToBeUpdated.setLast_name(updatedTelegramUser.getLast_name());
        telegramUserToBeUpdated.setUsername(updatedTelegramUser.getUsername());

        telegramUserToBeUpdated.setLanguage_code(updatedTelegramUser.getLanguage_code());
        telegramUserToBeUpdated.setIs_premium(updatedTelegramUser.getIs_premium());
        telegramUserToBeUpdated.setHash(updatedTelegramUser.getHash());
        telegramUserToBeUpdated.setAuth_date(updatedTelegramUser.getAuth_date());
//        telegramUserToBeUpdated.setCreatedAt(updatedTelegramUser.getCreatedAt());



        telegramUserRepository.save(telegramUserToBeUpdated);

    }

    @Transactional(readOnly = false)
    public void delete(int id) {
        TelegramUser foundTelegramUser = getTelegramUserByTgId(id).orElseThrow(TelegramUserNotFoundException::new);
        telegramUserRepository.deleteById(foundTelegramUser.getUser_id());
    }

    private void enrichTelegramUser(TelegramUser telegramUser) {
        telegramUser.setCreatedAt(LocalDateTime.now());
        // ?????????????????????????? ???????? ???????????????? ??????????, ???? ?????? ?????? ?? ?????????????? ?????????????? ?? ???????? ?????????? ?????? ?????????????????? ??????????-???? ???????????????? ???? ?????????????? ??????????????
        // private ??.??. ?????????????? ???????????? ???????????? ??????????????. ???????????? ???????????? ???? ??????????????????
    }
}
