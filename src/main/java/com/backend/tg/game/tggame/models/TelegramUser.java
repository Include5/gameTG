package com.backend.tg.game.tggame.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_user")
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

    private String auth_date;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public TelegramUser() {

    }

    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        this.query_id = query_id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAuth_date() {
        return auth_date;
    }

    public void setAuth_date(String auth_date) {
        this.auth_date = auth_date;
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
        if (query_id != null ? !query_id.equals(that.query_id) : that.query_id != null) return false;
        if (is_bot != null ? !is_bot.equals(that.is_bot) : that.is_bot != null) return false;
        if (first_name != null ? !first_name.equals(that.first_name) : that.first_name != null) return false;
        if (last_name != null ? !last_name.equals(that.last_name) : that.last_name != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (language_code != null ? !language_code.equals(that.language_code) : that.language_code != null)
            return false;
        if (is_premium != null ? !is_premium.equals(that.is_premium) : that.is_premium != null) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        if (auth_date != null ? !auth_date.equals(that.auth_date) : that.auth_date != null) return false;
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    @Override
    public int hashCode() {
        int result = query_id != null ? query_id.hashCode() : 0;
        result = 31 * result + user_id;
        result = 31 * result + id;
        result = 31 * result + (is_bot != null ? is_bot.hashCode() : 0);
        result = 31 * result + (first_name != null ? first_name.hashCode() : 0);
        result = 31 * result + (last_name != null ? last_name.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (language_code != null ? language_code.hashCode() : 0);
        result = 31 * result + (is_premium != null ? is_premium.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (auth_date != null ? auth_date.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}
