package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.auth.*;
import com.elice.ustory.domain.user.entity.EmailConfig;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.redis.email.AuthCode;
import com.elice.ustory.global.redis.email.AuthCodeForChangePwd;
import com.elice.ustory.global.redis.email.AuthCodeForChangePwdRepository;
import com.elice.ustory.global.redis.email.AuthCodeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final AuthCodeRepository authCodeRepository;
    private final AuthCodeForChangePwdRepository authCodeForChangePwdRepository;
    private final EmailConfig emailConfig;
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
                .filter(i -> (i < 57 || i >= 65) && (i <= 90 || i >= 97)) // ASCII 테이블에서 숫자, 대문자, 소문자만 사용함
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
        // 0. 이메일 중복 체크
        if (validateEmail(toEmail).getIsSuccess() == false) {
            throw new ValidationException("이미 가입된 이메일입니다.");
        };

        // 1. 메일 내용 생성
        String authCode = generateAuthCode();
        String title = "UStory 회원가입 인증코드입니다.";

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("UStory에 방문해주셔서 감사합니다.<br><br>")
                .append("인증 코드는 <code>")
                .append(authCode)
                .append("</code>입니다.<br>")
                .append("인증 코드를 바르게 입력해주세요.");

        String content = contentBuilder.toString();

        // 2. 인증코드를 Redis에 저장
        AuthCode authCodeObject = AuthCode.builder()
                .toEmail(toEmail)
                .authCode(authCode)
                .build();
        authCodeRepository.save(authCodeObject);

        // 3. 메일 발송
        sendMail(toEmail, title, content); // 생성된 메일 발송

        // 4. api 결괏값 반환
        log.info("[sendValidateSigunupResult] 인증코드 메일이 발송됨. 수신자 id : {}", userRepository.findByEmail(toEmail));
        AuthCodeCreateResponse authCodeCreateResponse = AuthCodeCreateResponse.builder()
                .fromMail(fromEmail)
                .toMail(toEmail)
                .title(title)
                .authCode(authCode)
                .build();

        return authCodeCreateResponse;
    }

    public AuthCodeVerifyResponse verifySignupAuthCode(AuthCodeVerifyRequest authCodeVerifyRequest) {
        String givenAuthCode = authCodeVerifyRequest.getAuthCode();
        String toMail = authCodeVerifyRequest.getToEmail();

        Optional<AuthCode> foundAuthCodeOptional = authCodeRepository.findById(toMail);

        if (foundAuthCodeOptional.isPresent()) {
            String foundAuthCode = foundAuthCodeOptional.get().getAuthCode();
            if (!foundAuthCode.equals(givenAuthCode)) {
                return AuthCodeVerifyResponse.builder()
                        .isValid(false)
                        .message("인증 코드 요청이 주어진 이메일이지만, 인증 코드가 일치하지 않습니다.")
                        .build();
            }
            return AuthCodeVerifyResponse.builder()
                    .isValid(true)
                    .message("이메일과 인증 코드가 일치하여, 유효한 인증 코드로 검증되었습니다.")
                    .build();
        } else {
            return AuthCodeVerifyResponse.builder()
                    .isValid(false)
                    .message("인증 코드 요청이 오지 않은 이메일입니다.")
                    .build();
        }
    }

    public EmailVerifyResponse validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            return EmailVerifyResponse.builder()
                    .isSuccess(false)
                    .status("EMAIL_DUPLICATE")
                    .build();
        }

        return EmailVerifyResponse.builder()
                .isSuccess(true)
                .status("SUCCESS")
                .build();
    }

    public ChangePwdCallResponse sendValidateUserMailForPwd(ChangePwdCallRequest changePwdCallRequest) throws MessagingException {
        // 0. 이메일 유효 체크
        String toEmail = changePwdCallRequest.getToEmail();
        if (!userRepository.existsByEmail(toEmail)) {
            return ChangePwdCallResponse.builder()
                    .message("가입된 이메일이라면 인증코드가 발송됩니다.")
                    .fromEmail(null)
                    .toEmail("가입되지 않은 이메일이므로 메일이 발송되지 않았습니다. 보안을 위해, 사용자에게 해당 이메일의 가입 여부를 반환하지 않습니다.")
                    .title(null)
                    .authCode(null)
                    .build();
        }

        // 1. 메일 내용 생성
        String authCode = generateAuthCode();
        String title = "UStory 비밀번호 변경을 위한 인증코드입니다.";

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("UStory에 방문해주셔서 감사합니다.<br><br>")
                .append("인증 코드는 <code>")
                .append(authCode)
                .append("</code>입니다.<br>")
                .append("인증 코드를 바르게 입력해주세요.");

        String content = contentBuilder.toString();

        // 2. 인증코드를 Redis에 저장
        AuthCodeForChangePwd authCodeForChangePwd = AuthCodeForChangePwd.builder()
                .toEmail(toEmail)
                .authCode(authCode)
                .build();
        authCodeForChangePwdRepository.save(authCodeForChangePwd);

        // 3. 메일 발송
        sendMail(toEmail, title, content);

        // 4. api 결괏값 반환
        return ChangePwdCallResponse.builder()
                .message("가입된 이메일이라면 인증코드가 발송됩니다.")
                .fromEmail(fromEmail)
                .toEmail(toEmail)
                .title(title)
                .authCode(authCode)
                .build();
    }

    public ChangePwdVerifyResponse verifyChangePwdCode(ChangePwdVerifyRequest changePwdVerifyRequest) {
        String givenAuthCode = changePwdVerifyRequest.getAuthCode();
        String toMail = changePwdVerifyRequest.getToEmail();

        Optional<AuthCodeForChangePwd> foundAuthCodeOptional = authCodeForChangePwdRepository.findById(toMail);

        if (foundAuthCodeOptional.isPresent()) {
            String foundAuthCode = foundAuthCodeOptional.get().getAuthCode();
            if (!foundAuthCode.equals(givenAuthCode)) {
                return ChangePwdVerifyResponse.builder()
                        .isValid(false)
                        .message("인증 코드 요청이 주어진 이메일이지만, 인증 코드가 일치하지 않습니다. 보안을 위해, 사용자에게 해당 이메일의 가입 여부를 반환하지 않습니다.")
                        .build();
            }
            return ChangePwdVerifyResponse.builder()
                    .isValid(true)
                    .message("이메일과 인증 코드가 일치하여, 유효한 인증 코드로 검증되었습니다.")
                    .build();
        } else {
            return ChangePwdVerifyResponse.builder()
                    .isValid(false)
                    .message("인증 코드 요청이 오지 않은 이메일입니다. 보안을 위해, 사용자에게 해당 이메일의 가입 여부를 반환하지 않습니다.")
                    .build();
        }
    }
}
