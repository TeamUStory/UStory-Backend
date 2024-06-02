package com.elice.ustory.domain.user.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    private String host = "smtp.gmail.com";

    private int port = 587;

    @Value("${mail.google.username}")
    private String username;

    @Value("${mail.google.password}")
    private String password;

    private boolean auth = true;

    private boolean starttlsEnable = true;

    private boolean starttlsRequired = true;

    private int connectionTimeout = 5000;

    private int timeout = 5000;

    private int writeTimeout = 5000;

    @Bean
    public JavaMailSender mailSender() { // Java MailSender 인터페이스를 구현한 객체를 빈으로 등록한다

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // Java MailSender 구현체를 생성 및 정의하낟
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        mailSender.setJavaMailProperties(getMailProperties()); // JavaMailSender에 생성된 JavaMailProperties 입력

        return mailSender; // 빈 등록
    }

    private Properties getMailProperties() {// JavaMailSender의 속성을 설정하기 위한 Properties 객체 생성
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", starttlsEnable);
        mailProperties.put("mail.smtp.starttls.required", starttlsRequired);
        mailProperties.put("mail.smtp.connectiontimeout", connectionTimeout);
        mailProperties.put("mail.smtp.timeout", timeout);
        mailProperties.put("mail.smtp.writetimeout", writeTimeout);

        return mailProperties;
    }

    public String getUsername() {
        return username;
    }
}
