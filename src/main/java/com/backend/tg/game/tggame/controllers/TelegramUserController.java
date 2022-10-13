package com.backend.tg.game.tggame.controllers;

import com.backend.tg.game.tggame.dto.TelegramUserDTO;
import com.backend.tg.game.tggame.dto.TelegramUserResponse;
import com.backend.tg.game.tggame.models.TelegramUser;
import com.backend.tg.game.tggame.security.JWTUtil;
import com.backend.tg.game.tggame.services.TelegramUserService;
import com.backend.tg.game.tggame.util.TelegramUserErrorResponse;
import com.backend.tg.game.tggame.util.TelegramUserNotCreatedException;
import com.backend.tg.game.tggame.util.TelegramUserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
        String initData = request.get("initData");
        String[] parsed = initData.split("hash=", 2);
        String hash = parsed[1];

        String decoded = URLDecoder.decode(parsed[0], "UTF-8");

        String[] data = decoded.split("&");
        Arrays.sort(data);

        String dataCheckString = String.join("\n", data);

        Boolean isValid = validateInitData(dataCheckString, hash);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("BadRequestException");
        }

        Integer auth_date = Integer.valueOf(data[0].split("auth_date=", 2)[1]);
        String query_id = data[1].split("query_id=", 2)[1];
        String userJsonString = data[2].split("user=", 2)[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            TelegramUser telegramUser = mapper.readValue(userJsonString, TelegramUser.class);
//            telegramUser.setQuery_id(query_id);
//            telegramUser.setAuth_date(auth_date);
//            telegramUser.setHash(hash);

            if (!telegramUserService.getTelegramUserByTgId(telegramUser.getId()).isPresent()) {
                // User does not exist. Create one
            } else {
                // User exists. Update one
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // return user
        return ResponseEntity.status(HttpStatus.OK).body("");
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

    private Boolean validateInitData(String dataCheckString, String hash) {

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            byte[] secret_key = sha256_HMAC.doFinal(Objects.requireNonNull(environment.getProperty("TELEGRAM_TOKEN")).getBytes(StandardCharsets.UTF_8));

            secretKeySpec = new SecretKeySpec(secret_key, "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            return hash.equals(Hex.encodeHexString(sha256_HMAC.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8))));
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
