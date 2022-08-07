package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.UserService;
import com.bcsdlab.biseo.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, String> signUp(UserRequest request) {
        if (userRepository.findByAccountId(request.getAccount_id()) != null) {
            throw new RuntimeException("존재하는 계정입니다.");
        }
        UserModel user = UserMapper.INSTANCE.toUserModel(request);

        // 기타 정보 처리
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setUser_type(UserType.NONE);

        // 학과처리
        if (request.getGrade() < 1 || request.getGrade() > 4) {
            throw new RuntimeException("잘못된 학년입니다.");
        }
        try {
            user.setDepartment(Department.valueOf(request.getDepartment()).getValue() + request.getGrade());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("존재하지 않는 학과입니다.");
        }

        // db 저장
        userRepository.signUp(user);

        // 저장 후 유저 정보 토큰으로 리턴
        Map<String, String> token = new HashMap<>();
        token.put("auth", jwtUtil.generateToken(user.getId(), 0));
        return token;
    }

    @Override
    public Map<String, String> login(UserRequest request) {
        UserModel user = userRepository.findByAccountId(request.getAccount_id());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, String> token = new HashMap<>();
        if (user.is_auth()) {
            token.put("access", jwtUtil.generateToken(user.getId(), 1));
            token.put("refresh", jwtUtil.generateToken(user.getId(), 2));
        } else {
            token.put("auth", jwtUtil.generateToken(user.getId(), 0));
        }

        return token;
    }

    @Override
    public Map<String, String> refresh() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        Map<String, Object> payloads = jwtUtil.getPayloads(token);
        Long id = Long.parseLong(payloads.get("id").toString());

        Map<String, String> newToken = new HashMap<>();
        newToken.put("access", jwtUtil.generateToken(id, 1));
        newToken.put("refresh", jwtUtil.generateToken(id, 2));
        return newToken;
    }
}
