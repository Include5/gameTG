package com.backend.tg.game.tggame.repositories;

import com.backend.tg.game.tggame.models.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Integer> {

    Optional<TelegramUser> findByTg_id(Integer id);
}
