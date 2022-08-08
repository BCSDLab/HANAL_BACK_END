package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.UserCertifiedModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.UserService;
import com.bcsdlab.biseo.util.JwtUtil;
import com.bcsdlab.biseo.util.MailUtil;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
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

    @Override
    public UserResponse signUp(UserRequest request) {
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
        return getUserResponse(user.getId());
    }

    @Override
    public Object login(UserRequest request) {
        UserModel user = userRepository.findByAccountId(request.getAccountId());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, String> token = new HashMap<>();
        if (user.getIsAuth()) {
            token.put("access", jwtUtil.generateToken(user.getId(),1,  1));
            token.put("refresh", jwtUtil.generateToken(user.getId(),2,  2));
            return token;
        } else {
            return getUserResponse(user.getId());
        }
    }

    @Override
    public Map<String, String> refresh() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        jwtUtil.isValid(token, 2);
        Long id = Long.parseLong(findUserInfoInToken().get("id").toString());

        Map<String, String> newToken = new HashMap<>();
        newToken.put("access", jwtUtil.generateToken(id, 1,1));
        newToken.put("refresh", jwtUtil.generateToken(id, 2, 2));
        return newToken;
    }

    @Override
    public Map<String, Long> sendAuthMail(UserRequest request) {
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
    public UserResponse getMe() {
        Map<String, Object> userInfo = findUserInfoInToken();
        Long id = Long.parseLong(userInfo.get("id").toString());

        return getUserResponse(id);
    }

    @Override
    public UserResponse updateDepartment(UserRequest request) {
        Map<String, Object> userInfo = findUserInfoInToken();
        Long id = Long.parseLong(userInfo.get("id").toString());
        UserModel user = new UserModel();
        user.setId(id);
        try {
            user.setDepartment(Department.valueOf(request.getDepartment()).getValue() + request.getGrade());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("존재하지 않는 학과입니다.");
        }

        userRepository.updateDepartment(user);

        return getUserResponse(id);
    }

    private UserResponse getUserResponse(Long id) {
        UserModel me = userRepository.findById(id);
        if (me == null) {
            throw new RuntimeException("잘못된 정보입니다.");
        }
        UserResponse response = UserMapper.INSTANCE.toUserResponse(me);
        response.setGrade(me.getDepartment() % 10);
        response.setDepartment(Department.getDepartment(me.getDepartment() / 10 * 10));
        return response;
    }

    private Map<String, Object> findUserInfoInToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        return jwtUtil.getPayloads(token);
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
