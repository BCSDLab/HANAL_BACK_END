package com.bcsdlab.biseo.serviceImpl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bcsdlab.biseo.dto.user.request.UserAuthDTO;
import com.bcsdlab.biseo.dto.user.request.JwtDTO;
import com.bcsdlab.biseo.dto.user.model.UserAuthModel;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.dto.user.request.UserDepartmentDTO;
import com.bcsdlab.biseo.dto.user.request.UserLoginDTO;
import com.bcsdlab.biseo.dto.user.request.UserPasswordDTO;
import com.bcsdlab.biseo.dto.user.request.UserSignUpDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import com.bcsdlab.biseo.enums.AuthType;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.ErrorMessage;
import com.bcsdlab.biseo.exception.AuthException;
import com.bcsdlab.biseo.exception.BadRequestException;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.UserService;
import com.bcsdlab.biseo.util.JwtUtil;
import com.bcsdlab.biseo.util.MailUtil;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
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
    public UserResponseDTO signUp(UserSignUpDTO request) {
        if (userRepository.findByAccountId(request.getAccountId()) != null) {
            throw new BadRequestException(ErrorMessage.USER_ID_EXIST);
        }
        UserModel user = UserMapper.INSTANCE.toUserModel(request);

        // db 저장
        userRepository.signUp(user);

        // 저장 후 이메일 인증을 위해 유저 정보 리턴
        return getUserResponse(userRepository.findById(user.getId()));
    }

    @Override
    public JwtDTO login(UserLoginDTO request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new BadRequestException(ErrorMessage.USER_ID_NOT_EXIST);
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.PASSWORD_INVALID);
        }

        if (!user.getIsAuth()) {
            throw new AuthException(ErrorMessage.USER_NOT_AUTHORIZATION);
        }

        JwtDTO response = new JwtDTO();

        // 200
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String accessToken = jwtUtil.getAccessToken(user);
        String key = String.format("user:%s:refresh", user.getId());
        Optional<String> refreshTokenOptional = Optional.ofNullable(
            operations.get(key)).filter(t -> !jwtUtil.isExpired(t));

        String refreshToken = refreshTokenOptional.orElseGet(() -> {
            String newToken = jwtUtil.getRefreshToken(user);
            operations.set(key, newToken);
            return newToken;
        });

        return new JwtDTO(accessToken, refreshToken);
    }

    @Override
    public String logout() {
        DecodedJWT decodedJWT = jwtUtil.findUserInfoInToken();

        String key = String.format("user:%s:refresh", decodedJWT.getAudience().get(0));
        stringRedisTemplate.delete(key);
        return "로그아웃 완료";
    }

    @Override
    public JwtDTO refresh() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String refreshToken = this.checkRefreshToken(request.getHeader("Authorization"));

        DecodedJWT decodedRefreshJWT = jwtUtil.getDecodedJWT(refreshToken);

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String key = String.format("user:%s:refresh", decodedRefreshJWT.getAudience().get(0));
        String storedRefreshToken = Optional.ofNullable(operations.get(key))
            .filter(t -> t.equals(refreshToken))
            .orElseThrow(() -> new AuthException(ErrorMessage.REFRESH_TOKEN_EXPIRED));

        // 문제가 없으면 토큰 정보로 회원 조회 후 다시 토큰 생성
        Long userId = Long.parseLong(decodedRefreshJWT.getAudience().get(0));
        UserModel user = userRepository.findById(userId);
        String accessToken = jwtUtil.getAccessToken(user);

        return new JwtDTO(accessToken);
    }

    @Override
    public UserAuthDTO sendAuthMail(String accountId, AuthType authType) {
        if (accountId == null || accountId.equals("")) {
            throw new BadRequestException(ErrorMessage.USER_NOT_EXIST);
        }
        UserModel user = userRepository.findByAccountId(accountId);
        if (user == null) {
            throw new BadRequestException(ErrorMessage.USER_NOT_EXIST);
        }

        // 이전 발송 메일이랑 시간 차이 계산
        UserAuthModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(user.getAccountId(), authType);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (recentAuthNum != null
            && now.getTime() - recentAuthNum.getCreatedAt().getTime() < 5 * 60 * 1000) {
            throw new BadRequestException(ErrorMessage.SEND_MAIL_FAIL);
        }
        if (recentAuthNum != null) {
            userRepository.deleteAuthNumById(recentAuthNum.getId());
        }

        // 인증번호 저장
        String authNum = makeRandomNumber();
        UserAuthModel userAuthModel = new UserAuthModel();
        userAuthModel.setUserId(user.getId());
        userAuthModel.setAuthNum(authNum);
        userAuthModel.setAuthType(authType);
        userRepository.addAuthNum(userAuthModel);

        // 메일 발송
        mailUtil.sendAuthCodeMail(user, authNum);

        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setAccountId(user.getAccountId());
        return userAuthDTO;
    }

    @Override
    public void authorizeSignUpMail(UserAuthDTO userAuthDTO) {
        UserAuthModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(
            userAuthDTO.getAccountId(), AuthType.SIGNUP);

        if (recentAuthNum == null) {
            throw new BadRequestException(ErrorMessage.SEND_MAIL_FIRST);
        }

        if (!userAuthDTO.getAuthCode().equals(recentAuthNum.getAuthNum())) {
            throw new BadRequestException(ErrorMessage.AUTH_CODE_INVALID);
        }

        userRepository.deleteAuthNumById(recentAuthNum.getId());
        userRepository.setUserAuth(userRepository.findByAccountId(userAuthDTO.getAccountId()).getId());
    }

    @Override
    public void authorizePasswordMail(UserAuthDTO userAuthDTO) {
        UserAuthModel recentAuthNum = userRepository.findRecentAuthNumByUserAccountId(
            userAuthDTO.getAccountId(), AuthType.PASSWORD);

        if (recentAuthNum == null) {
            throw new BadRequestException(ErrorMessage.SEND_MAIL_FIRST);
        }

        if (!userAuthDTO.getAuthCode().equals(recentAuthNum.getAuthNum())) {
            throw new BadRequestException(ErrorMessage.AUTH_CODE_INVALID);
        }

        userRepository.deleteAuthNumById(recentAuthNum.getId());
        UserModel user = userRepository.findById(recentAuthNum.getUserId());
        String newPassword = makeNewPassword();
        // 비밀번호 저장
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepository.updatePassword(user);
        // 비밀번호 전송
        mailUtil.sendPasswordMail(user, newPassword);
    }

    @Override
    public UserResponseDTO getMe() {
        DecodedJWT userInfo = jwtUtil.findUserInfoInToken();
        Long id = Long.valueOf(userInfo.getAudience().get(0));

        return getUserResponse(userRepository.findById(id));
    }

    @Override
    public UserResponseDTO updateDepartment(UserDepartmentDTO request) {
        DecodedJWT userInfo = jwtUtil.findUserInfoInToken();
        UserModel user = UserMapper.INSTANCE.departmentDtotoUserModel(request);
        user.setId(Long.parseLong(userInfo.getAudience().get(0)));

        userRepository.updateDepartment(user);

        return getUserResponse(userRepository.findById(user.getId()));
    }

    @Override
    public void updatePassword(UserPasswordDTO passwordDTO) {
        DecodedJWT decodedJWT = jwtUtil.findUserInfoInToken();
        UserModel user = userRepository.findById(Long.parseLong(decodedJWT.getAudience().get(0)));

        if (!BCrypt.checkpw(passwordDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.CHANGE_PASSWORD_FAIL);
        }

        user.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword(), BCrypt.gensalt()));
        userRepository.updatePassword(user);
    }

    private UserResponseDTO getUserResponse(UserModel me) {
        if (me == null) {
            throw new BadRequestException(ErrorMessage.USER_NOT_EXIST);
        }
        UserResponseDTO response = UserMapper.INSTANCE.toUserResponse(me);
        response.setGrade(me.getDepartment() % 10);
        response.setDepartment(Department.getDepartment(me.getDepartment() / 10 * 10));
        return response;
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
        final char[] charSet = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z',
            '!', '@', '#', '$', '%', '^', '&'};

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int len = charSet.length;
        for (int i = 0; i < 10; i++) {
            sb.append(charSet[random.nextInt(len)]);
        }

        return sb.toString();
    }

    private String checkRefreshToken(String bearerToken) {
        if (!jwtUtil.isValidForm(bearerToken)) {
            throw new AuthException(ErrorMessage.REFRESH_TOKEN_INVALID);
        }
        String token = bearerToken.substring(7);
        if (!jwtUtil.isValid(token, "refresh")) {
            throw new AuthException(ErrorMessage.REFRESH_TOKEN_EXPIRED);
        }

        return token;
    }
}
