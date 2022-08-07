package com.bcsdlab.biseo.interceptor;

import com.bcsdlab.biseo.annotation.Auth;
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

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new RuntimeException("토큰이 존재하지 않습니다.");
        }

        return jwtUtil.isValid(authorization, auth.type());
    }
}
