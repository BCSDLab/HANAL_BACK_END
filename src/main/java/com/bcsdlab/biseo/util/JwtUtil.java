package com.bcsdlab.biseo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
}
