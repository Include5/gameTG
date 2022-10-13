package com.backend.tg.game.tggame.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_user")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TelegramUser {

    @Column(name = "query_id")
    private String query_id;
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @Column(name = "id")
    private int id;

    @Column(name = "is_bot")
    private Boolean is_bot;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "username")
    private String username;

    @Column(name = "language_code")
    private String language_code;

    @Column(name = "is_premium")
    private Boolean is_premium;

    @Column(name = "photo_url")
    private String photo_url;

    private String hash;

    private Integer auth_date;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
