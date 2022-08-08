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

    public String generateToken(Long id, Integer type, Integer isValid) {
        /**
         * 1 : access
         * 2 : refresh
         */
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id);
        payloads.put("type", type);
        payloads.put("auth", isValid);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String key = null;

        // TODO : 인증 시간 변경 필요
        switch (type) {
            case 1:
                key = access;
                calendar.add(Calendar.MINUTE, 10);
                break;
            case 2:
                key = refresh;
                calendar.add(Calendar.MINUTE, 40);
                break;
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

    public boolean isValid(String token, int type) {
        if (token == null) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        if (token.length() < 8 || !token.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String jwt = token.substring(7);

        Map<String, Object> payloads = getPayloads(jwt);
        int auth = -1;
        int tokenType = -1;
        Long id = -1L;
        try {
            auth = Integer.parseInt(payloads.get("auth").toString());
            id = Long.parseLong(payloads.get("id").toString());
            tokenType = Integer.parseInt(payloads.get("type").toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        if (id < 0 || auth < 0 || auth > 2 || tokenType < 1 || tokenType > 2) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String key = null;
        if (type == 1) {
            key = access;
        } else if (type == 2) {
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
            if (type == 1) {
                if (auth == 0 && tokenType == 1) {
                    throw new RuntimeException("인증 토큰이 만료되었습니다.");
                } else if (auth == 1 && tokenType == 1) {
                    throw new RuntimeException("Access 토큰이 만료되었습니다.");
                } else {
                    throw new RuntimeException(("유효하지 않은 토큰입니다."));
                }
            } else {
                if (auth == 2 && tokenType == 2) {
                    throw new RuntimeException("Refresh 토큰이 만료되었습니다.");
                } else {
                    throw new RuntimeException(("유효하지 않은 토큰입니다."));
                }
            }
        }
        return true;
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        if (map.get("id") == null || map.get("auth") == null || map.get("type") == null) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        return map;
    }
}
