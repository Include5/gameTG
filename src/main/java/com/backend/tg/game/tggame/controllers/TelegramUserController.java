package com.backend.tg.game.tggame.controllers;

import com.backend.tg.game.tggame.dto.TelegramUserDTO;
import com.backend.tg.game.tggame.dto.TelegramUserResponse;
import com.backend.tg.game.tggame.models.TelegramUser;
import com.backend.tg.game.tggame.services.TelegramUserService;
import com.backend.tg.game.tggame.util.TelegramUserErrorResponse;
import com.backend.tg.game.tggame.util.TelegramUserNotCreatedException;
import com.backend.tg.game.tggame.util.TelegramUserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tg_users")
public class TelegramUserController {

    private final TelegramUserService telegramUserService;

    private final ModelMapper modelMapper;

    @Autowired
    public TelegramUserController(TelegramUserService telegramUserService, ModelMapper modelMapper) {
        this.telegramUserService = telegramUserService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public TelegramUserResponse getTelegramUsers() {
        return new TelegramUserResponse(telegramUserService.findAll().stream().map(this::convertToTelegramUserDTO)
                .collect(Collectors.toList()));
        // telegramUserService находит все юзеров в бд, после стримами по каждому объект проходимся и переводим в DTO
        // собираем всё в список ArrayList и через фреймворк Jackson отдаётся json ответом
    }

    @PostMapping("")
    public ResponseEntity<HttpStatus> createTelegramUser(@RequestBody @Valid TelegramUserDTO telegramUserDTO,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new TelegramUserNotCreatedException(errorMsg.toString());
        }

        if (telegramUserService.getTelegramUserByTgId(telegramUserDTO.getTgid()).isPresent())
            throw new TelegramUserNotCreatedException("User is already exists");

        telegramUserService.save(convertToTelegramUser(telegramUserDTO));

        return ResponseEntity.ok(HttpStatus.CREATED);

    }

    @PatchMapping()
    public ResponseEntity<HttpStatus> updateTelegramUser(@RequestBody @Valid TelegramUserDTO telegramUserDTO,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new TelegramUserNotCreatedException(errorMsg.toString()); // создать отдельную ошибку
        }

        if (telegramUserService.getTelegramUserByTgId(telegramUserDTO.getTgid()).isEmpty())
            throw new TelegramUserNotFoundException();

        telegramUserService.update(convertToTelegramUser(telegramUserDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteTelegramUser(@PathVariable("id") int id) {
        telegramUserService.delete(id);
    }

    @ExceptionHandler
    private ResponseEntity<TelegramUserErrorResponse> handleException(TelegramUserNotCreatedException e) {
        TelegramUserErrorResponse response = new TelegramUserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TelegramUserErrorResponse> handleException(TelegramUserNotFoundException e) {
        TelegramUserErrorResponse response = new TelegramUserErrorResponse(
                "User with selected TG id not found",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private TelegramUser convertToTelegramUser(TelegramUserDTO telegramUserDTO) {
        return modelMapper.map(telegramUserDTO, TelegramUser.class);
        // Переводит из TelegramUserDTO в TelegramUser
    }

    private TelegramUserDTO convertToTelegramUserDTO(TelegramUser telegramUser) {
        return modelMapper.map(telegramUser, TelegramUserDTO.class);
        // Переводит из TelegramUser в TelegramUserDTO
    }
}
