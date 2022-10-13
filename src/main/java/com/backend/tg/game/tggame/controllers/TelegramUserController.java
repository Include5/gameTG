package com.backend.tg.game.tggame.controllers;

import com.backend.tg.game.tggame.dto.TelegramUserDTO;
import com.backend.tg.game.tggame.dto.TelegramUserResponse;
import com.backend.tg.game.tggame.models.TelegramUser;
import com.backend.tg.game.tggame.security.JWTUtil;
import com.backend.tg.game.tggame.services.TelegramUserService;
import com.backend.tg.game.tggame.util.TelegramUserErrorResponse;
import com.backend.tg.game.tggame.util.TelegramUserNotCreatedException;
import com.backend.tg.game.tggame.util.TelegramUserNotFoundException;
import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PropertySource("classpath:telegram.properties")
@RestController
@RequestMapping("/api/tg_users")
public class TelegramUserController {

    private final JWTUtil jwTutil;

    private final TelegramUserService telegramUserService;

    private final Environment environment;

    private final ModelMapper modelMapper;

    @Autowired
    public TelegramUserController(JWTUtil jwTutil, TelegramUserService telegramUserService, Environment environment, ModelMapper modelMapper) {
        this.jwTutil = jwTutil;
        this.telegramUserService = telegramUserService;
        this.environment = environment;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public TelegramUserResponse getTelegramUsers() {
        return new TelegramUserResponse(telegramUserService.findAll().stream().map(this::convertToTelegramUserDTO)
                .collect(Collectors.toList()));
        // telegramUserService находит все юзеров в бд, после стримами по каждому объект проходимся и переводим в DTO
        // собираем всё в список ArrayList и через фреймворк Jackson отдаётся json ответом
    }

    @PostMapping("auth/telegram")
    @ResponseBody
    public ResponseEntity<Object> telegramAuth(@RequestBody Map<String, String> request) throws UnsupportedEncodingException {

        // возможно стоит вынести в метод и переименовать переменные
        String s = String.valueOf(request.values()).replace("[", "").replace("]", "");
        String[] parsed = s.split("hash=", 2);
        String encoded = URLDecoder.decode(parsed[0], "UTF-8");
        String[] array = encoded.split("&");
        Arrays.sort(array);
        String dataCheckString = String.join("\n", array);

        // парсим данные для занесения в БД
        ArrayList<String> list = userDataParse(dataCheckString);

        TelegramUserDTO telegramUserDTO = new TelegramUserDTO();
        telegramUserDTO.setId(Integer.parseInt(list.get(0)));
        telegramUserDTO.setFirst_name(list.get(1));
        telegramUserDTO.setLast_name(list.get(2));
        telegramUserDTO.setUsername(list.get(3));
        telegramUserDTO.setLanguage_code(list.get(4));
        telegramUserDTO.setAuth_date(list.get(5));
        telegramUserDTO.setQuery_id(list.get(6));

        String _hash = telegramTokenCheck(dataCheckString);


        if (!telegramUserService.getTelegramUserByTgId(Integer.valueOf(list.get(0))).isPresent()) {

            if (parsed[1].compareToIgnoreCase(_hash) == 0) {
                telegramUserService.save(convertToTelegramUser(telegramUserDTO));
                // возвращаем токен

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        "Hash not equals"
                );
            }
        }

        if (telegramUserService.getTelegramUserByTgId(Integer.valueOf(list.get(0))).isPresent()) {
            TelegramUser telegramUser = telegramUserService.getTelegramUserByTgId(Integer.valueOf(list.get(0))).get();

            parsed[1] = telegramUser.getHash();

            if (parsed[1].compareToIgnoreCase(_hash) == 0) {
                // возвращаем токен
            } else {
                telegramUserService.update(convertToTelegramUser(telegramUserDTO));
                // поправит хуйню шоб норм было
                if (parsed[1].compareToIgnoreCase(_hash) == 0) {
                    // возвращать хеш
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            "Hash not equals"
                    );
                }
            }

        }

        // если юзер есть по айди - берём хеш и бд - сравниваем с текущим - не совпадает -
        // обновляем и возвращаем токен - совпадает - возвроащаем токен

        // если юзера нет - проверяем хеш - создаём аккаунт - возвращаем токен
                // возвращать ошибку
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        "govno"
                );
    }

    @PostMapping("")
    public ResponseEntity<Object> createTelegramUser(@RequestBody @Valid TelegramUserDTO telegramUserDTO,
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

        if (telegramUserService.getTelegramUserByTgId(telegramUserDTO.getId()).isPresent())
            throw new TelegramUserNotCreatedException("User is already exists");

        telegramUserService.save(convertToTelegramUser(telegramUserDTO));

        String token = jwTutil.generateToken(telegramUserDTO.getUsername());

        return ResponseEntity.ok(token);

    }

    @PatchMapping("")
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

//        if (telegramUserService.getTelegramUserByTgId(telegramUserDTO.getTgid()))
//            throw new TelegramUserNotFoundException();

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
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(telegramUserDTO, TelegramUser.class);
        // Переводит из TelegramUserDTO в TelegramUser
    }

    private TelegramUserDTO convertToTelegramUserDTO(TelegramUser telegramUser) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(telegramUser, TelegramUserDTO.class);
        // Переводит из TelegramUser в TelegramUserDTO
    }

    private String telegramTokenCheck(String dataCheckString) {

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            byte[] secret_key = sha256_HMAC.doFinal(Objects.requireNonNull(environment.getProperty("TELEGRAM_TOKEN")).getBytes(StandardCharsets.UTF_8));

            secretKeySpec = new SecretKeySpec(secret_key, "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            return Hex.encodeHexString(sha256_HMAC.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> userDataParse(String dataCheckString) {

        String[] temp = dataCheckString.split("\\\"\\w+\\\"\\:");
        ArrayList<String> list = new ArrayList<>();

        for (int i = 1; i < temp.length; i++) {
            list.add(temp[i].replace("\"", "").replace("}", "").replace(",", "").replace("=", ""));
        }

        Pattern pattern = Pattern.compile("\\=\\w+", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(dataCheckString);

        while (matcher.find()) {
            list.add(matcher.group().replace("=", ""));
        }

        return list;
    }

}
