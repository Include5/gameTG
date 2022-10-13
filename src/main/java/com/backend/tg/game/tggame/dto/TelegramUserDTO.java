package com.backend.tg.game.tggame.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
public class TelegramUserDTO {

    private String query_id;

    private int id;

    private String first_name;

    private String last_name;

    private String username;

    private String language_code;

    private Integer auth_date;

    private String hash;
}
