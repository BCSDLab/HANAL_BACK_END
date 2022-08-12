package com.bcsdlab.biseo.serviceImpl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bcsdlab.biseo.dto.user.CertificationCodeDTO;
import com.bcsdlab.biseo.dto.user.JwtDTO;
import com.bcsdlab.biseo.dto.user.UserCertifiedModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserPasswordDTO;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
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
            user.setDepartment(
                Department.valueOf(request.getDepartment()).getValue() + request.getGrade());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("존재하지 않는 학과입니다.");
        }

        // db 저장
        userRepository.signUp(user);

        // 저장 후 이메일 인증을 위해 유저 정보 리턴
        return getUserResponse(userRepository.findById(user.getId()));
    }

    @Override
    public JwtDTO login(UserRequestDTO request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        JwtDTO response = new JwtDTO();
        response.setUserAccountId(user.getAccountId());

        // 401?
        if (!user.getIsAuth()) {
            return response;
        }

        // 200
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String accessToken = jwtUtil.getAccessToken(user);
        String key = String.format("user:%s:refresh", user.getAccountId());
        Optional<String> refreshTokenOptional = Optional.ofNullable(
                operations.get(user.getAccountId()))
            .filter(t -> !jwtUtil.isExpired(t));

        String refreshToken = refreshTokenOptional.orElseGet(() -> {
            String newToken = jwtUtil.getRefreshToken(user);
            operations.set(key, newToken);
            return newToken;
        });

        response.setAccess(accessToken);
        response.setRefresh(refreshToken);
        return response;
    }

    @Override
    public String logout() {
        DecodedJWT decodedJWT = findUserInfoInToken();
        UserModel user = userRepository.findById(Long.parseLong(decodedJWT.getAudience().get(0)));

        String key = String.format("user:%s:refresh", user.getAccountId());
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();

        String refreshToken = operations.get(key);
        if (refreshToken != null) {
            // ValueOperations 는 delete 가 없다고 한다....
            stringRedisTemplate.delete(key);
        }
        return "로그아웃 완료";
    }

    @Override
    public JwtDTO refresh(JwtDTO jwtDTO) {
        if (!jwtUtil.isValid(jwtDTO.getRefresh(), "refresh")) {
            throw new RuntimeException("토큰이 만료되었거나, 올바르지 않습니다.");
        }
        DecodedJWT decodedRefreshJWT = jwtUtil.getDecodedJWT(jwtDTO.getRefresh());
        Long userId = Long.parseLong(decodedRefreshJWT.getAudience().get(0));
        UserModel user = userRepository.findById(userId);

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String key = String.format("user:%s:refresh", user.getAccountId());
        String refreshToken = Optional.ofNullable(operations.get(key))
            .filter(t -> t.equals(jwtDTO.getRefresh()))
            .orElseThrow(() -> new RuntimeException("올바르지 않은 토큰입니다. 다시 로그인해야 합니다."));

        String accessToken = jwtUtil.getAccessToken(user);

        // 3일 뒤 만료라면
        if (LocalDateTime.now().plusDays(3).isAfter(
            decodedRefreshJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime())) {
            refreshToken = jwtUtil.getRefreshToken(user);
            operations.set(key, refreshToken);
        }

        return new JwtDTO(user.getAccountId(), accessToken, refreshToken);
    }

    @Override
    public Map<String, String> sendAuthMail(UserRequestDTO request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 이전 발송 메일이랑 시간 차이 계산
        UserCertifiedModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(user.getAccountId());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (recentAuthNum != null
            && now.getTime() - recentAuthNum.getCreatedAt().getTime() < 5 * 60 * 1000) {
            throw new RuntimeException("메일 전송이 불가능합니다.");
        }
        if (recentAuthNum != null) {
            userRepository.deleteAuthNumById(recentAuthNum.getId());
        }
        // 메일 발송
        String authNum = makeRandomNumber();
        mailUtil.sendAuthCodeMail(user, authNum);

        // 인증번호 저장
        UserCertifiedModel userCertifiedModel = new UserCertifiedModel();
        userCertifiedModel.setUserId(user.getId());
        userCertifiedModel.setAuthNum(authNum);
        userRepository.addAuthNum(userCertifiedModel);

        Map<String, String> map = new HashMap<>();
        map.put("accountId", user.getAccountId());
        return map;
    }

    @Override
    public String certifySignUpMail(CertificationCodeDTO certificationCodeDTO) {
        UserCertifiedModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(
            certificationCodeDTO.getAccountId());

        if (recentAuthNum == null) {
            throw new RuntimeException("이메일 인증을 먼저 해주세요");
        }

        if (!certificationCodeDTO.getAuthCode().equals(recentAuthNum.getAuthNum())) {
            throw new RuntimeException("인증번호가 다릅니다. 인증번호를 확인해주세요");
        }

        userRepository.deleteAuthNumById(recentAuthNum.getId());
        userRepository.setUserAuth(userRepository.findByAccountId(certificationCodeDTO.getAccountId()).getId());
        return "인증이 완료되었습니다.";
    }

    @Override
    public String certifyPasswordMail(CertificationCodeDTO certificationCodeDTO) {
        UserCertifiedModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(
            certificationCodeDTO.getAccountId());

        if (recentAuthNum == null) {
            throw new RuntimeException("이메일 인증을 먼저 해주세요");
        }

        if (!certificationCodeDTO.getAuthCode().equals(recentAuthNum.getAuthNum())) {
            throw new RuntimeException("인증번호가 다릅니다. 인증번호를 확인해주세요");
        }

        userRepository.deleteAuthNumById(recentAuthNum.getId());
        UserModel user = userRepository.findById(recentAuthNum.getUserId());
        String newPassword = makeNewPassword();
        mailUtil.sendPasswordMail(user, newPassword);
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepository.updatePassword(user);

        return "비밀번호가 메일로 전송되었습니다.";
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
            user.setDepartment(
                Department.valueOf(request.getDepartment()).getValue() + request.getGrade());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("존재하지 않는 학과입니다.");
        }

        userRepository.updateDepartment(user);

        return getUserResponse(userRepository.findById(id));
    }

    @Override
    public String updatePassword(UserPasswordDTO passwordDTO) {
        DecodedJWT decodedJWT = findUserInfoInToken();
        UserModel user = userRepository.findById(Long.parseLong(decodedJWT.getAudience().get(0)));

        if (!BCrypt.checkpw(passwordDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("기존 비밀번호가 다릅니다.");
        }

        user.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword(), BCrypt.gensalt()));
        userRepository.updatePassword(user);
        return "비밀번호가 변경되었습니다.";
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

    private String makeNewPassword() {
        final char[] charSet = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '!', '@', '#', '$', '%', '^', '&' };

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int len = charSet.length;
        for (int i=0; i<10; i++) {
            sb.append(charSet[random.nextInt(len)]);
        }

        return sb.toString();
    }
}
