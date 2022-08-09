package com.bcsdlab.biseo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.enums.UserType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secret;
    private Algorithm key;

    @PostConstruct
    public void setKey() {
        key = Algorithm.HMAC256(secret);
    }

    public String getAccessToken(UserModel user) {
        return JWT.create()
            .withSubject("access")
            .withAudience(user.getId().toString())
            .withClaim("type", user.getUserType().getLevel())
            .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant()))
            .sign(key);
    }

    public String getRefreshToken(UserModel user) {
        return JWT.create()
            .withSubject("refresh")
            .withAudience(user.getId().toString())
            .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
            .sign(key);
    }

    public boolean isValid(String token, String tokenType) {
        try {
            return !JWT.require(key)
                .withSubject(tokenType)
                .build()
                .verify(token)
                .getAudience()
                .isEmpty();
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            JWT.require(key).build().verify(token);
        } catch (TokenExpiredException e) {
            return true;
        }
        return false;
    }

    public DecodedJWT getDecodedJWT(String token) {
        try {
            return JWT.decode(token);
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
