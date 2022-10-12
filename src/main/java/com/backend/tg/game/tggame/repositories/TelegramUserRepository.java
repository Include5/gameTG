package com.backend.tg.game.tggame.repositories;

import com.backend.tg.game.tggame.models.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Integer> {

    @Query("SELECT tgu FROM TelegramUser tgu WHERE tgu.id = ?1")
    Optional<TelegramUser> findByTelegramId(int id);
}
