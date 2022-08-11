package com.bcsdlab.biseo.serviceImpl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.AuthDTO;
import com.bcsdlab.biseo.dto.user.UserCertifiedModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.UserService;
import com.bcsdlab.biseo.util.JwtUtil;
import com.bcsdlab.biseo.util.MailUtil;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final MailUtil mailUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserResponseDTO signUp(UserRequestDTO request) {
        if (userRepository.findByAccountId(request.getAccountId()) != null) {
            throw new RuntimeException("존재하는 계정입니다.");
        }
        UserModel user = UserMapper.INSTANCE.toUserModel(request);

        // 기타 정보 처리
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setUserType(UserType.NONE);

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

        // 저장 후 이메일 인증을 위해 유저 정보 리턴
        return getUserResponse(userRepository.findById(user.getId()));
    }

    @Override
    public AuthDTO login(UserRequestDTO request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, String> token = new HashMap<>();
        if (user.getIsAuth()) {
            // 200
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String accessToken = jwtUtil.getAccessToken(user);
            String key = String.format("user:%s:refresh", user.getAccountId());
            Optional<String> refreshTokenOptional = Optional.ofNullable(operations.get(user.getAccountId()))
                .filter(t -> !jwtUtil.isExpired(t));

            String refreshToken = refreshTokenOptional.orElseGet(() ->{
                String newToken = jwtUtil.getRefreshToken(user);
                operations.set(key, newToken);
                return newToken;
            });
            return new AuthDTO(user.getId(), accessToken, refreshToken);
        } else {
            // 401?
            AuthDTO response = new AuthDTO();
            response.setUserId(user.getId());
            return response;
        }
    }

    @Override
    public AuthDTO refresh(AuthDTO authDTO) {
        if (!jwtUtil.isValid(authDTO.getRefresh(), "refresh")){
            throw new RuntimeException("토큰이 만료되었거나, 올바르지 않습니다.");
        }
        DecodedJWT decodedRefreshJWT = jwtUtil.getDecodedJWT(authDTO.getRefresh());
        Long userId = Long.parseLong(decodedRefreshJWT.getAudience().get(0));
        UserModel user = userRepository.findById(userId);

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String key = String.format("user:%s:refresh", user.getAccountId());
        String refreshToken = Optional.ofNullable(operations.get(key))
            .filter(t -> t.equals(authDTO.getRefresh()))
            .orElseThrow(() -> new RuntimeException("올바르지 않은 토큰입니다. 다시 로그인해야 합니다."));

        String accessToken = jwtUtil.getAccessToken(user);

        // 3일 뒤 만료라면
        if (LocalDateTime.now().plusDays(3).isAfter(decodedRefreshJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            refreshToken = jwtUtil.getRefreshToken(user);
            operations.set(key, refreshToken);
        }

        return new AuthDTO(user.getId(), accessToken, refreshToken);
    }

    @Override
    public Map<String, Long> sendAuthMail(UserRequestDTO request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 이전 발송 메일이랑 시간 차이 계산
        UserCertifiedModel recentAuthNum = userRepository.findRecentAuthNumByUserId(user.getId());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (recentAuthNum != null && now.getTime() - recentAuthNum.getCreatedAt().getTime() < 5 * 60 * 1000) {
            throw new RuntimeException("메일 전송이 불가능합니다.");
        }
        if (recentAuthNum != null) {
            userRepository.deleteAuthNumById(recentAuthNum.getId());
        }
        // 메일 발송
        String authNum = makeRandomNumber();
        mailUtil.sendMail(user, authNum);

        // 인증번호 저장
        UserCertifiedModel userCertifiedModel = new UserCertifiedModel();
        userCertifiedModel.setUserId(user.getId());
        userCertifiedModel.setAuthNum(authNum);
        userRepository.addAuthNum(userCertifiedModel);

        Map<String, Long> map = new HashMap<>();
        map.put("userId", user.getId());
        return map;
    }

    @Override
    public String verifyAuthMail(AuthCode authCode) {
        UserCertifiedModel recentAuthNum = userRepository.findRecentAuthNumByUserId(authCode.getUserId());

        if (recentAuthNum == null) {
            throw new RuntimeException("이메일 인증을 먼저 해주세요");
        }

        if (!authCode.getAuthCode().equals(recentAuthNum.getAuthNum())) {
            throw new RuntimeException("인증번호가 다릅니다. 인증번호를 확인해주세요");
        }

        userRepository.deleteAuthNumById(recentAuthNum.getId());
        userRepository.setUserAuth(authCode.getUserId());
        return "인증이 완료되었습니다.";
    }

    @Override
    public UserResponseDTO getMe() {
        DecodedJWT userInfo = findUserInfoInToken();
        Long id = Long.valueOf(userInfo.getAudience().get(0));

        return getUserResponse(userRepository.findById(id));
    }

    @Override
    public UserResponseDTO updateDepartment(UserRequestDTO request) {
        DecodedJWT userInfo = findUserInfoInToken();
        Long id = Long.valueOf(userInfo.getAudience().get(0));
        UserModel user = new UserModel();
        user.setId(id);
        try {
            user.setDepartment(Department.valueOf(request.getDepartment()).getValue() + request.getGrade());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("존재하지 않는 학과입니다.");
        }

        userRepository.updateDepartment(user);

        return getUserResponse(userRepository.findById(id));
    }

    private UserResponseDTO getUserResponse(UserModel me) {
        if (me == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        UserResponseDTO response = UserMapper.INSTANCE.toUserResponse(me);
        response.setGrade(me.getDepartment() % 10);
        response.setDepartment(Department.getDepartment(me.getDepartment() / 10 * 10));
        return response;
    }

    private DecodedJWT findUserInfoInToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        return jwtUtil.getDecodedJWT(token.substring(7));
    }

    private String makeRandomNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
