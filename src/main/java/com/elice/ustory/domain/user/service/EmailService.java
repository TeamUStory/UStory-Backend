package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.AuthCodeCreateResponse;
import com.elice.ustory.domain.user.entity.EmailConfig;
import com.elice.ustory.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    // private final RedisUtil redisUtil; // TODO: Redis 추가 후 주석 해제
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final EmailConfig emailConfig; // TODO: 이렇게 Config를 끌고와도 되는건지?
    private String fromEmail;

    @PostConstruct
    private void init() {
        fromEmail = emailConfig.getUsername(); // emailConfig 객체가 먼저 초기화된 후 getUsername() 메서드 호출
    }

    public String generateAuthCode() {
        int leftLimit = 48; // 숫자 '0'의 ASCII 코드
        int rightLimit = 122; // 알파벳 'z'의 ASCII 코드
        int stringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1) // leftLimit(포함) 부터 rightLimit+1(불포함) 사이의 난수 스트림 생성
                .filter(i -> (i < 57 || i >= 65) && ( i <= 90 || i >= 97)) // ASCII 테이블에서 숫자, 대문자, 소문자만 사용함
                .limit(stringLength) // 생성된 난수를 지정된 길이로 잘라냄
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // 생성된 난수를 ASCII 테이블에서 대응되는 문자로 변환
                .toString(); // StringBuilder 객체를 문자열로 변환해 반환
    }

    public void sendMail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage(); // JavaMailSender 객체를 이용해 MimeMessage 객체 생성

        mimeMessage.addRecipients(MimeMessage.RecipientType.TO, toEmail);
        mimeMessage.setSubject(title);
        mimeMessage.setFrom(fromEmail);
        mimeMessage.setText(content, "utf-8", "html");

        javaMailSender.send(mimeMessage);
    }

    public AuthCodeCreateResponse sendValidateSignupMail(String toEmail) throws MessagingException {
        String authCode = generateAuthCode();
        String title = "UStory 회원가입 인증코드입니다.";
        String content =
                "Ustory에 방문해주셔서 감사합니다.<br><br>"
                        + "인증 코드는 <code>" + authCode + "</code>입니다.<br>"
                        + "인증 코드를 바르게 입력해주세요."
                ;

        sendMail(toEmail, title, content); // 생성된 메일 발송
//        redisUtil.setDataExpire(toEmail, authCode, 60 * 30L); // TODO: Redis에 인증코드 유효시간 설정

        log.info("[sendValidateSigunupResult] 인증코드 메일이 발송됨. 수신자 id : {}", userRepository.findByEmail(toEmail));
        AuthCodeCreateResponse authCodeCreateResponse = AuthCodeCreateResponse.builder()
                .fromMail(fromEmail)
                .toMail(toEmail)
                .title(title)
                .authCode(authCode)
                .build();

        return authCodeCreateResponse;
    }
}
