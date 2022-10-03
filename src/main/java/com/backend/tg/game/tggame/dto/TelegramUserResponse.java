package com.backend.tg.game.tggame.dto;

import java.util.List;

public class TelegramUserResponse {

    private List<TelegramUserDTO> telegramUsers;

    public TelegramUserResponse(List<TelegramUserDTO> telegramUsers) {
        this.telegramUsers = telegramUsers;
    }

    public List<TelegramUserDTO> getTelegramUsers() {
        return telegramUsers;
    }

    public void setTelegramUsers(List<TelegramUserDTO> telegramUsers) {
        this.telegramUsers = telegramUsers;
    }
}
