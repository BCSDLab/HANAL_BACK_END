package com.bcsdlab.biseo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.access}")
    private String access;

    @Value("${jwt.refresh}")
    private String refresh;

    public String generateToken(Long id, Integer type) {
        /**
         * 0 : 인증 안됨
         * 1 : 인증 된 access
         * 2 : 인증 된 refresh
         */
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id);
        payloads.put("auth", type);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String key = null;

        // TODO : 인증 시간 변경 필요
        switch (type) {
            case 0:
            case 1:
                key = access;
                calendar.add(Calendar.MINUTE, 10);
                break;
            case 2:
                key = refresh;
                calendar.add(Calendar.MINUTE, 40);
            default:
                throw new RuntimeException("잘못된 인증 타입입니다.");
        }
        Date exp = calendar.getTime();
        return Jwts.builder()
            .setHeader(header)
            .setClaims(payloads)
            .setExpiration(exp)
            .signWith(SignatureAlgorithm.HS256, key.getBytes())
            .compact();
    }

    public boolean isValid(String token, int authType) {
        if (token == null) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        if (token.length() < 8 || !token.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String jwt = token.substring(7);
        int jwtType = -1;
        try {
            jwtType = Integer.parseInt(getPayloads(jwt).get("auth").toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String key = null;
        if (authType == 0 || authType == 1) {
            key = access;
        } else if (authType == 2) {
            key = refresh;
        } else {
            throw new RuntimeException("잘못된 인증 타입입니다.");
        }

        try {
            Claims claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
        } catch (ExpiredJwtException expiredJwtException) {
            if (authType == 0) {
                if (jwtType == 0) {
                    throw new RuntimeException("인증 토큰이 만료되었습니다.");
                } else {
                    throw new RuntimeException("유효하지 않은 토큰입니다.");
                }
            } else {
                if (authType == 1 && jwtType == 1) {
                    throw new RuntimeException("Access 토큰이 만료되었습니다.");
                } else if (authType == 2 && jwtType == 2) {
                    throw new RuntimeException("Refresh 토큰이 만료되었습니다.");
                } else {
                    throw new RuntimeException(("유효하지 않은 토큰입니다."));
                }
            }
        }
        if (authType == jwtType) {
            return true;
        } else {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    public Map<String, Object> getPayloads(String token) {
        String[] tmp = token.split("\\.");
        if (tmp.length != 3) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        String payloads = new String(Base64.getDecoder().decode(tmp[1]));
        HashMap<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(payloads, HashMap.class);
            if (map.get("id") == null || map.get("auth") == null) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        return map;
    }
}
