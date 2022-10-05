package com.backend.tg.game.tggame.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_user")
public class TelegramUser {

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

    @Column(name = "user_name")
    private String user_name;

    @Column(name = "language_code")
    private String language_сode;

    @Column(name = "is_premium")
    private Boolean is_premium;

    @Column(name = "photo_url")
    private String photo_url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TelegramUser() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getIs_bot() {
        return is_bot;
    }

    public void setIs_bot(Boolean is_bot) {
        this.is_bot = is_bot;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLanguage_сode() {
        return language_сode;
    }

    public void setLanguage_сode(String language_сode) {
        this.language_сode = language_сode;
    }

    public Boolean getIs_premium() {
        return is_premium;
    }

    public void setIs_premium(Boolean is_premium) {
        this.is_premium = is_premium;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelegramUser that = (TelegramUser) o;

        if (user_id != that.user_id) return false;
        if (id != that.id) return false;
        if (is_bot != null ? !is_bot.equals(that.is_bot) : that.is_bot != null) return false;
        if (first_name != null ? !first_name.equals(that.first_name) : that.first_name != null) return false;
        if (last_name != null ? !last_name.equals(that.last_name) : that.last_name != null) return false;
        if (user_name != null ? !user_name.equals(that.user_name) : that.user_name != null) return false;
        if (language_сode != null ? !language_сode.equals(that.language_сode) : that.language_сode != null)
            return false;
        if (is_premium != null ? !is_premium.equals(that.is_premium) : that.is_premium != null) return false;
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    @Override
    public int hashCode() {
        int result = user_id;
        result = 31 * result + id;
        result = 31 * result + (is_bot != null ? is_bot.hashCode() : 0);
        result = 31 * result + (first_name != null ? first_name.hashCode() : 0);
        result = 31 * result + (last_name != null ? last_name.hashCode() : 0);
        result = 31 * result + (user_name != null ? user_name.hashCode() : 0);
        result = 31 * result + (language_сode != null ? language_сode.hashCode() : 0);
        result = 31 * result + (is_premium != null ? is_premium.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}
