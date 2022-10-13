package com.backend.tg.game.tggame.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.time.ZonedDateTime;

@Component
public class JWTUtil {

    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        // 60 минут действиe токена

        return JWT.create()
                .withSubject("Telegram user details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("Quiz game")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256("secret"));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("secret"))
                .withSubject("Telegram user details")
                .withIssuer("Quiz game")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}
