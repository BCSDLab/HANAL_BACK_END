package com.bcsdlab.biseo.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.util.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        Auth auth = method.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }
        // 토큰 길이 인증
        String bearerToken = request.getHeader("Authorization");
        if (!jwtUtil.isValidForm(bearerToken)) {
            throw new RuntimeException("올바르지 않은 토큰입니다.");
        }
        String token = bearerToken.substring(7);

        // 토큰 만료 인증
        if (jwtUtil.isExpired(token)) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }

        // 토큰 타입 인증
        if (!jwtUtil.isValid(token, "access")) {
            throw new RuntimeException("잘못된 토큰입니다.");
        }

        // 권한 인증
        DecodedJWT decodedJWT = jwtUtil.getDecodedJWT(token);
        if (decodedJWT.getClaim("type").asInt() < auth.type().getLevel()) {
            throw new RuntimeException("해당 서비스에 대한 권한이 없습니다.");
        }

        return true;
    }
}
