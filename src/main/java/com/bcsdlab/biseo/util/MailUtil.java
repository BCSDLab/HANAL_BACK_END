package com.bcsdlab.biseo.util;

import com.bcsdlab.biseo.dto.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;

    public void sendMail(UserModel user, String authNum) {

        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(user.getAccountId() + "@koreatech.ac.kr");
        simpleMessage.setSubject("인증번호 전송");
        simpleMessage.setText(authNum);
        mailSender.send(simpleMessage);
    }
}
