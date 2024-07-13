package com.elice.ustory.global.util;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NicknameGenerator {
    private static final int NICKNAME_MAX_LENGTH = RegexPatterns.NICKNAME_MAX_LENGTH;
    private static final String SEPARATOR = "#"; // 닉네임과 임의값 사이의 구분자

    private static final int POSTFIX_LENGTH = 2;

    public String generateRandomPostfix() {
        int leftLimit = 48; // 숫자 '0'의 ASCII 코드
        int rightLimit = 122; // 알파벳 'z'의 ASCII 코드
        int postfixLength = POSTFIX_LENGTH;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1) // leftLimit(포함) 부터 rightLimit+1(불포함) 사이의 난수 스트림 생성
                .filter(i -> (i < 57 || i >= 65) && ( i <= 90 || i >= 97)) // ASCII 테이블에서 숫자, 대문자, 소문자만 사용함
                .limit(postfixLength) // 생성된 난수를 지정된 길이로 잘라냄
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // 생성된 난수를 ASCII 테이블에서 대응되는 문자로 변환
                .toString(); // StringBuilder 객체를 문자열로 변환해 반환
    }

    public String normalizeNicknameForOAuth(String socialNickname) {
        // 정규식에 맞지 않는 글자를 제외
        String cleanedNickname = socialNickname.replaceAll(RegexPatterns.NICKNAME_REG_LETTER_FOR_REPLACE, "");
        return cleanedNickname;
    }

    public String 임의의값을_추가하는_함수(String cleanedNickname) {
        //TODO
        return "String";
    }

    public String trimNicknameForOAuth(String cleanedNickname) {
        if (cleanedNickname == null) {
            return null;
        }
        String trimmedNickname = cleanedNickname.length() > NICKNAME_MAX_LENGTH ? cleanedNickname.substring(0, NICKNAME_MAX_LENGTH) : cleanedNickname;
        return trimmedNickname;
    }
}
