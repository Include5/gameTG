package com.backend.tg.game.tggame.util;

public class TelegramUserErrorResponse {

    private String message;

    private long timestamp;
    // для логирования ошибок хотя нахуй это нахуй это надо не знаю ахахха как будто кто-то будет тут дебажить чёт


    public TelegramUserErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
